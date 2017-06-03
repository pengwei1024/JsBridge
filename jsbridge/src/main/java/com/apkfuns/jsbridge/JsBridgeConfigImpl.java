package com.apkfuns.jsbridge;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JsPromptResult;

import com.alibaba.fastjson.JSON;
import com.apkfuns.jsbridge.util.IPromptResult;
import com.apkfuns.jsbridge.util.IWebView;

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
    public JsBridgeConfig setLoadReadyFuncName(String readyName) {
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
        StringBuilder builder = new StringBuilder();
        builder.append("var JsBridgeClass_ABABABA = function () {");
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
                setContextMethod.setAccessible(true);
                if (setContextMethod != null) {
                    setContextMethod.invoke(null, context);
                }
                Method setWebViewMethod = module.getClass().getMethod("setWebView", IWebView.class);
                setWebViewMethod.setAccessible(true);
                if (setWebViewMethod != null) {
                    setWebViewMethod.invoke(null, webView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.append("JsBridgeClass_ABABABA.prototype." + module.getModuleName() + " = {");
            HashMap<String, JsMethod> methods = getExposedMethods().get(module);
            for (String method : methods.keySet()) {
                JsMethod jsMethod = methods.get(method);
                builder.append(jsMethod.getInjectJs());
            }
            builder.append("};");
        }
        builder.append("};");
        builder.append("window." + getProtocol() + " = new JsBridgeClass_ABABABA();");
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
                    argumentParser.getParameters();
                    try {
                        Object ret = method.invoke("");
                        setJsPromptResult(result, true, ret.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            List<JBArgumentParser.Parameter> parameters = argumentParser.getParameters();
            if (parameters != null) {
                for (JBArgumentParser.Parameter param : parameters) {

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
