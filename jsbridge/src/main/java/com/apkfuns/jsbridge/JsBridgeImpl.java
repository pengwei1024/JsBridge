package com.apkfuns.jsbridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pengwei on 2017/6/9.
 */

class JsBridgeImpl extends JsBridge {

    private Object mWebView;
    private final JsBridgeConfigImpl config;
    private final List<JsModule> loadModule;
    private final Map<JsModule, HashMap<String, JsMethod>> exposedMethods;
    private final String className;
    private String preLoad;
    private final Handler handler;
    private final Set<String> moduleLayers;
    private String newProtocol;
    private String newLoadReadyMethod;

    JsBridgeImpl(JsModule... modules) {
        this(null, null, modules);
    }

    JsBridgeImpl(String protocol, String readyMethod, JsModule... modules) {
        config = JsBridgeConfigImpl.getInstance();
        className = "JB_" + Integer.toHexString(hashCode());
        loadModule = new ArrayList<>();
        exposedMethods = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        moduleLayers = new HashSet<>();
        this.newProtocol = protocol;
        this.newLoadReadyMethod = readyMethod;
        if (TextUtils.isEmpty(newProtocol)) {
            newProtocol = config.getProtocol();
        }
        if (TextUtils.isEmpty(newLoadReadyMethod)) {
            newLoadReadyMethod = config.getReadyFuncName();
        }
        loadingModule(modules);
        JBLog.d(String.format("Protocol:%s, LoadReadyMethod:%s, moduleSize:%s",
                newProtocol, newLoadReadyMethod, loadModule.size()));
    }

    /**
     * load module
     */
    private void loadingModule(JsModule... modules) {
        try {
            for (Class<? extends JsModule> moduleCls : config.getDefaultModule()) {
                JsModule module = moduleCls.newInstance();
                if (module != null && !TextUtils.isEmpty(module.getModuleName())) {
                    loadModule.add(module);
                }
            }
            if (modules != null) {
                for (JsModule module : modules) {
                    if (module != null && !TextUtils.isEmpty(module.getModuleName())) {
                        loadModule.add(module);
                    }
                }
            }
            if (!loadModule.isEmpty()) {
                Collections.sort(loadModule, new ModuleComparator());
                for (JsModule module : loadModule) {
                    HashMap<String, JsMethod> methodsMap = JBUtils.getAllMethod(
                            module, module.getClass(), newProtocol);
                    if (!methodsMap.isEmpty()) {
                        exposedMethods.put(module, methodsMap);
                    }
                }
            }
        } catch (Exception e) {
            JBLog.e("loadingModule error", e);
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
                if (preLoad == null) {
                    preLoad = getInjectJsString();
                }
                for (JsModule module : loadModule) {
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
                        JBLog.e("JsModule set Context Error", e);
                    }
                }
                evaluateJavascript(preLoad);
                JBLog.d("onInjectJs finish");
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
        evaluateJavascript(newProtocol + "=undefined;");
    }

    @Override
    public final void release() {
        for (JsModule module : exposedMethods.keySet()) {
            module.mWebView = null;
            module.mContext = null;
        }
        exposedMethods.clear();
        JBLog.d("JsBridge destroy");
    }

    @Override
    public void evaluateJavascript(final String jsCode) {
        if (mWebView == null) {
            JBLog.d("Please call injectJs first");
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
        builder.append(JBUtilMethodFactory.getUtilMethods(newLoadReadyMethod));
        // 注入默认方法
        for (JsModule module : loadModule) {
            HashMap<String, JsMethod> methods = exposedMethods.get(module);
            if (module instanceof JsStaticModule) {
                for (String method : methods.keySet()) {
                    JsMethod jsMethod = methods.get(method);
                    builder.append(jsMethod.getInjectJs());
                }
            } else {
                List<String> moduleGroup = JBUtils.moduleSplit(module.getModuleName());
                if (moduleGroup.isEmpty()) {
                    continue;
                } else {
                    for (int i = 0; i < moduleGroup.size() - 1; ++i) {
                        if (!moduleLayers.contains(moduleGroup.get(i))) {
                            for (int k = i; k < moduleGroup.size() - 1; ++k) {
                                builder.append(className + ".prototype." + moduleGroup.get(k) + " = {};");
                                moduleLayers.add(moduleGroup.get(k));
                            }
                            break;
                        }
                    }
                    builder.append(className + ".prototype." + module.getModuleName() + " = {");
                    moduleLayers.add(module.getModuleName());
                }
                for (String method : methods.keySet()) {
                    JsMethod jsMethod = methods.get(method);
                    builder.append(jsMethod.getInjectJs());
                }
                builder.append("};");
            }
        }
        builder.append("};");
        builder.append("window." + newProtocol + " = new " + className + "();");
        builder.append(newProtocol + ".OnJsBridgeReady();");
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
                        JBLog.e("Call JsMethod <" + method.getMethodName() + "> Error", e);
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

    /**
     * sort by module
     */
    private static class ModuleComparator implements Comparator<JsModule> {

        @Override
        public int compare(JsModule lhs, JsModule rhs) {
            return lhs.getModuleName().split("\\.").length - rhs.getModuleName().split("\\.").length;
        }
    }
}
