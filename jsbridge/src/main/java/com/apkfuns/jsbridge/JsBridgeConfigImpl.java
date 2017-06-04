package com.apkfuns.jsbridge;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JsPromptResult;

import com.alibaba.fastjson.JSON;
import com.apkfuns.jsbridge.util.IPromptResult;
import com.apkfuns.jsbridge.util.JBArgumentErrorException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pengwei on 16/5/13.
 */
class JsBridgeConfigImpl implements JsBridgeConfig {

    private Map<JsModule, HashMap<String, JsMethod>> exposedMethods = new HashMap<>();
    private Set<Class<? extends JsRunMethod>> methodRuns = new HashSet<>();
    private String protocol;
    private String readyFuncName;

    private JsBridgeConfigImpl() {
    }

    private static JsBridgeConfigImpl singleton;

    public static JsBridgeConfigImpl getInstance() {
        if (singleton == null) {
            synchronized (JsBridgeConfigImpl.class) {
                if (singleton == null) {
                    singleton = new JsBridgeConfigImpl();
                }
            }
        }
        return singleton;
    }

    public Set<Class<? extends JsRunMethod>> getMethodRuns() {
        return methodRuns;
    }

    @Override
    public JsBridgeConfig registerModule(Class<? extends JsModule>... modules) {
        if (modules != null && modules.length > 0) {
            for (Class<? extends JsModule> moduleCls : modules) {
                try {
                    JsModule module = moduleCls.newInstance();
                    if (module != null) {
                        HashMap<String, JsMethod> methodsMap = Utils.getAllMethod(
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
        return this;
    }

    public Map<JsModule, HashMap<String, JsMethod>> getExposedMethods() {
        return exposedMethods;
    }

    @Override
    public JsBridgeConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getProtocol() {
        return TextUtils.isEmpty(protocol) ? DEFAULT_PROTOCOL : protocol;
    }

    @Override
    public JsBridgeConfig setLoadReadyMethod(String readyName) {
        this.readyFuncName = readyName;
        return this;
    }

    public String getReadyFuncName() {
        if (TextUtils.isEmpty(this.readyFuncName)) {
            return String.format("on%sReady", getProtocol());
        }
        return readyFuncName;
    }

    public JsModule getModule(String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            throw new NullPointerException("Module name is empty");
        }
        for (JsModule module : getExposedMethods().keySet()) {
            if (moduleName.equals(module.getModuleName())) {
                return module;
            }
        }
        return null;
    }

    /**
     * 注入JS
     *
     * @param context
     * @param webView
     * @return
     */
    public final String getInjectJsString(Context context, Object webView) {
        String className = "JsBridgeClass_" + System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();
        builder.append("var " + className + " = function () {");
        // 注入通用方法
        builder.append(JBUtilMethodFactory.getUtilMethods());
        // 注入默认方法
        for (JsModule module : getExposedMethods().keySet()) {
            if (module == null || TextUtils.isEmpty(module.getModuleName())) {
                continue;
            }
            // 为JsModule设置context 和 WebView
            try {
                Method setContextMethod = module.getClass().getMethod("setContext", Context.class);
                if (setContextMethod != null) {
                    setContextMethod.invoke(module, context);
                }
                Method setWebViewMethod = module.getClass().getMethod("setWebView", Object.class);
                if (setWebViewMethod != null) {
                    setWebViewMethod.invoke(module, webView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.append(className + ".prototype." + module.getModuleName() + " = {");
            HashMap<String, JsMethod> methods = getExposedMethods().get(module);
            for (String method : methods.keySet()) {
                JsMethod jsMethod = methods.get(method);
                builder.append(jsMethod.getInjectJs());
            }
            builder.append("};");
        }
        builder.append("};");
        builder.append("window." + getProtocol() + " = new " + className + "();");
        builder.append(getProtocol() + ".OnJsBridgeReady();");
        Log.e("****", builder.toString());
        return builder.toString();
    }

    /**
     * 执行js回调
     *
     * @param methodArgs
     * @param result
     */
    public final void callJsPrompt(String methodArgs, Object result) {
        if (TextUtils.isEmpty(methodArgs) || result == null) {
            throw new NullPointerException("JsPrompt Arguments Null");
        }
        JBArgumentParser argumentParser = JSON.parseObject(methodArgs, JBArgumentParser.class);
        if (argumentParser != null && !TextUtils.isEmpty(argumentParser.getModule())
                && !TextUtils.isEmpty(argumentParser.getMethod())) {
            JsModule findModule = getModule(argumentParser.getModule());
            if (findModule != null) {
                HashMap<String, JsMethod> methodHashMap = getExposedMethods().get(findModule);
                if (methodHashMap != null && !methodHashMap.isEmpty() && methodHashMap.containsKey(
                        argumentParser.getMethod())) {
                    JsMethod method = methodHashMap.get(argumentParser.getMethod());
                    List<JBArgumentParser.Parameter> parameters = argumentParser.getParameters();
                    int length = method.getParameterType().size();
                    Object[] invokeArgs = new Object[length];
                    for (int i = 0; i < length; ++i) {
                        int type = method.getParameterType().get(i);
                        if (parameters != null && parameters.size() >= i + 1) {
                            JBArgumentParser.Parameter param = parameters.get(i);
                            Object parseObject = Utils.parseToObject(type, param,
                                    method.getCallback(), findModule.getWebViewObject());
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
                        return;
                    } catch (Exception e) {
                        setJsPromptResult(result, false, e.getMessage());
                    }
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
