package com.apkfuns.jsbridge;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by pengwei on 16/5/6.
 */
public class JSBridge {

    private static JsBridgeConfigImpl config = JsBridgeConfigImpl.getInstance();

    static {
        config.registerMethodRun(JSBridgeReadyRun.class);
    }

    public static JsBridgeConfig getConfig() {
        return config;
    }

    /**
     * 注入JS
     */
    public static void injectJs(WebView webView) {
        if (webView == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        // 注入默认方法
        builder.append("window." + config.getProtocol() + " = {");
        for (JsModule module : config.getExposedMethods().keySet()) {
            if (module == null || TextUtils.isEmpty(module.getModuleName())) {
                continue;
            }
            // 为JsModule设置context
            try {
                Method contextMethod = module.getClass().getMethod("setContext", Context.class);
                contextMethod.setAccessible(true);
                if (contextMethod != null) {
                    contextMethod.invoke(null, webView.getContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.append(module.getModuleName() + ":{");
            HashMap<String, JsMethod> methods = config.getExposedMethods().get(module);
            for (String method : methods.keySet()) {
                JsMethod jsMethod = methods.get(method);
                builder.append(jsMethod.getInjectJs());
            }
            builder.append("},");
        }
        builder.append("};");
        // 注入可执行方法
        for (Class<? extends JsMethodRun> run : config.getMethodRuns()) {
            try {
                builder.append(run.newInstance().execJs());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Log.wtf("injectJs", builder.toString());
        webView.loadUrl("javascript:" + builder.toString());
    }

    /**
     * 执行js回调
     *
     * @param webView
     * @param uriString
     */
    public static void callJsPrompt(Activity activity, WebView webView, String uriString, JsPromptResult result) {
        if (result == null) {
            throw new NullPointerException("JsPromptResult must not null");
        }
        if (TextUtils.isEmpty(uriString)) {
            throw new NullPointerException("uriString must not null");
        }
        Uri uri = Uri.parse(uriString);
        String methodName = "";
        String className = "";
        String param = "";
        String port = "";
        if (uriString.startsWith(config.getProtocol())) {
            className = uri.getHost();
            param = uri.getQuery();
            String path = uri.getPath();
            port = uri.getPort() + "";
            if (!TextUtils.isEmpty(path)) {
                methodName = path.replace("/", "");
            }
        }
        if (config.getExposedMethods().containsKey(className)) {
            HashMap<String, JsMethod> methodHashMap = config.getExposedMethods().get(className);
            if (methodHashMap != null && methodHashMap.size() != 0 && methodHashMap.containsKey(methodName)) {
                JsMethod method = methodHashMap.get(methodName);
                if (method == null) {
                    result.confirm();
                    return;
                }
                if (method instanceof JsMethodExt) {
                    ((JsMethodExt) method).handleResult(result, activity, param);
                } else {
                    if (method != null && method.getJavaMethod() != null && method.getParameterType() != -1) {
                        try {
                            if (method.getParameterType() == ParameterType.TYPE_AWOJR) {
                                method.invoke(activity, webView, param, new JSCallback(webView, method, port),
                                        new JsReturn(result));
                                return;
                            }
                            Object ret = null;
                            switch (method.getParameterType()) {
                                case ParameterType.TYPE_O:
                                    ret = method.invoke(param);
                                    break;
                                case ParameterType.TYPE_AO:
                                    ret = method.invoke(activity, param);
                                    break;
                                case ParameterType.TYPE_WO:
                                    ret = method.invoke(webView, param);
                                    break;
                                case ParameterType.TYPE_AWO:
                                    ret = method.invoke(activity, webView, param);
                                    break;
                                case ParameterType.TYPE_AOJ:
                                    ret = method.invoke(activity, param, new JSCallback(webView, method, port));
                                    break;
                                case ParameterType.TYPE_OJ:
                                    ret = method.invoke(param, new JSCallback(webView, method, port));
                                    break;
                                case ParameterType.TYPE_WOJ:
                                    ret = method.invoke(webView, param, new JSCallback(webView, method, port));
                                    break;
                                case ParameterType.TYPE_AWOJ:
                                    ret = method.invoke(activity, webView, param, new JSCallback(webView, method, port));
                                    break;
                                default:
                                    break;
                            }
                            if (ret != null) {
                                result.confirm(ret.toString());
                            } else {
                                result.confirm();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                result.confirm();
            }
        } else {
            result.confirm();
        }
    }

    /**
     * 执行javascript文本
     * @param webView
     * @param script
     */
    public static void evaluateJavascript(WebView webView, String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, null);
        } else {
            webView.loadUrl("javascript:" + script);
        }
    }
}
