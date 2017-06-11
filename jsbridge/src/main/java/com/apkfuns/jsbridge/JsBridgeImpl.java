package com.apkfuns.jsbridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.apkfuns.jsbridge.common.IPromptResult;
import com.apkfuns.jsbridge.common.IWebView;
import com.apkfuns.jsbridge.common.JBArgumentErrorException;
import com.apkfuns.jsbridge.module.JSArgumentType;
import com.apkfuns.jsbridge.module.JsModule;
import com.apkfuns.jsbridge.module.JsStaticModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pengwei on 2017/6/9.
 */

class JsBridgeImpl extends JsBridge {

    private Object mWebView;
    private final JsBridgeConfigImpl config;
    private final List<Class<? extends JsModule>> loadModule;
    private final Map<JsModule, HashMap<String, JsMethod>> exposedMethods;
    private final String className;
    private String preLoad;
    private Handler handler;

    JsBridgeImpl(Class<? extends JsModule>... modules) {
        className = "JsBridgeClass_" + System.currentTimeMillis();
        loadModule = new ArrayList<>();
        exposedMethods = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        config = JsBridgeConfigImpl.getInstance();
        loadModule.addAll(config.getDefaultModule());
        if (modules != null) {
            for (Class<? extends JsModule> moduleCls : modules) {
                loadModule.add(moduleCls);
            }
        }
        loadModule();
        preLoad = getInjectJsString();
        if (config.isDebug()) {
            Log.d(TAG, "init JsBridge");
        }
    }

    /**
     * load module by class
     */
    private void loadModule() {
        if (!loadModule.isEmpty()) {
            for (Class<? extends JsModule> moduleCls : loadModule) {
                try {
                    JsModule module = moduleCls.newInstance();
                    if (module != null) {
                        HashMap<String, JsMethod> methodsMap = JBUtils.getAllMethod(
                                module, moduleCls);
                        if (!methodsMap.isEmpty()) {
                            exposedMethods.put(module, methodsMap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public final void injectJs(@NonNull WebView webView) {
        onInjectJs(webView.getContext(), webView);
    }

    @Override
    public final void injectJs(@NonNull IWebView webView) {
        onInjectJs(webView.getContext(), webView);
    }

    private void onInjectJs(final Context context, final Object webView) {
        this.mWebView = webView;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (JsModule module : exposedMethods.keySet()) {
                    // 为JsModule设置context 和 WebView
                    if (module.mContext != null && module.mContext.getClass().equals(context.getClass())) {
                        break;
                    }
                    try {
                        Field contextField = module.getClass().getField("mContext");
                        if (contextField != null) {
                            contextField.set(module, context);
                        }
                        Field webViewField = module.getClass().getField("mWebView");
                        if (webViewField != null) {
                            webViewField.set(module, webView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                evaluateJavascript(preLoad);
                if (config.isDebug()) {
                    Log.d(TAG, "onInjectJs finish");
                }
            }
        }, "JsBridgeThread").start();
    }

    @Override
    public final void callJsPrompt(@NonNull String methodArgs, @NonNull JsPromptResult result) {
        onCallJsPrompt(methodArgs, result);
    }

    @Override
    public final void callJsPrompt(@NonNull String methodArgs, @NonNull IPromptResult result) {
        onCallJsPrompt(methodArgs, result);
    }

    @Override
    public final void clean() {
        evaluateJavascript(config.getProtocol() + "=undefined;");
    }

    @Override
    public final void release() {
        exposedMethods.clear();
        if (config.isDebug()) {
            Log.d(TAG, "JsBridge destroy");
        }
    }

    /**
     * evaluate javascript
     * @param jsCode
     */
    private void evaluateJavascript(final String jsCode) {
        if (mWebView == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mWebView instanceof WebView) {
                    ((WebView) mWebView).loadUrl("javascript:" + jsCode);
                } else if (mWebView instanceof IWebView) {
                    ((IWebView) mWebView).loadUrl("javascript:" + jsCode);
                } else {
                    throw new JBArgumentErrorException("Can not cast " + mWebView.getClass().getSimpleName()
                            + " to WebView");
                }
            }
        });
    }

    private JsModule getModule(String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            throw new NullPointerException("Module name is empty");
        }
        for (JsModule module : exposedMethods.keySet()) {
            if (moduleName.equals(module.getModuleName())) {
                return module;
            }
        }
        return null;
    }

    private String getInjectJsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("var " + className + " = function () {");
        // 注入通用方法
        builder.append(JBUtilMethodFactory.getUtilMethods());
        // 注入默认方法
        for (JsModule module : exposedMethods.keySet()) {
            if (module == null || TextUtils.isEmpty(module.getModuleName())) {
                continue;
            }
            HashMap<String, JsMethod> methods = exposedMethods.get(module);
            if (module instanceof JsStaticModule) {
                for (String method : methods.keySet()) {
                    JsMethod jsMethod = methods.get(method);
                    builder.append(jsMethod.getInjectJs());
                }
            } else {
                builder.append(className + ".prototype." + module.getModuleName() + " = {");
                for (String method : methods.keySet()) {
                    JsMethod jsMethod = methods.get(method);
                    builder.append(jsMethod.getInjectJs());
                }
                builder.append("};");
            }
        }
        builder.append("};");
        builder.append("window." + config.getProtocol() + " = new " + className + "();");
        builder.append(config.getProtocol() + ".OnJsBridgeReady();");
        return builder.toString();
    }

    /**
     * 执行js回调
     *
     * @param methodArgs
     * @param result
     */
    private void onCallJsPrompt(String methodArgs, Object result) {
        if (TextUtils.isEmpty(methodArgs) || result == null) {
            throw new NullPointerException("JsPrompt Arguments Null");
        }
        JBArgumentParser argumentParser = JSON.parseObject(methodArgs, JBArgumentParser.class);
        if (argumentParser != null && !TextUtils.isEmpty(argumentParser.getModule())
                && !TextUtils.isEmpty(argumentParser.getMethod())) {
            JsModule findModule = getModule(argumentParser.getModule());
            if (findModule != null) {
                HashMap<String, JsMethod> methodHashMap = exposedMethods.get(findModule);
                if (methodHashMap != null && !methodHashMap.isEmpty() && methodHashMap.containsKey(
                        argumentParser.getMethod())) {
                    JsMethod method = methodHashMap.get(argumentParser.getMethod());
                    List<JBArgumentParser.Parameter> parameters = argumentParser.getParameters();
                    int length = method.getParameterType().size();
                    Object[] invokeArgs = new Object[length];
                    for (int i = 0; i < length; ++i) {
                        @JSArgumentType.Type int type = method.getParameterType().get(i);
                        if (parameters != null && parameters.size() >= i + 1) {
                            JBArgumentParser.Parameter param = parameters.get(i);
                            Object parseObject = JBUtils.parseToObject(type, param, method);
                            if (parseObject != null && parseObject instanceof JBArgumentErrorException) {
                                setJsPromptResult(result, false, parseObject.toString());
                                return;
                            }
                            invokeArgs[i] = parseObject;
                        }
                        if (invokeArgs[i] == null) {
                            switch (type) {
                                case JSArgumentType.TYPE_NUMBER:
                                    invokeArgs[i] = 0;
                                    break;
                                case JSArgumentType.TYPE_BOOL:
                                    invokeArgs[i] = false;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    try {
                        Object ret = method.invoke(invokeArgs);
                        setJsPromptResult(result, true, ret == null ? "" : ret.toString());
                    } catch (Exception e) {
                        setJsPromptResult(result, false, e.getMessage());
                    }
                    return;
                }
            }
        }
        setJsPromptResult(result, false, "JBArgument Parse error");
    }

    private void setJsPromptResult(Object promptResult, boolean success, String msg) {
        JSONObject ret = new JSONObject();
        try {
            ret.put("success", success);
            ret.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (promptResult instanceof JsPromptResult) {
            ((JsPromptResult) promptResult).confirm(ret.toString());
        } else if (promptResult instanceof IPromptResult) {
            ((IPromptResult) promptResult).confirm(ret.toString());
        } else {
            throw new IllegalArgumentException("JsPromptResult Error");
        }
    }
}
