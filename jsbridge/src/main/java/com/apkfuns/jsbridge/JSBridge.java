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

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by pengwei on 16/5/6.
 */
public class JSBridge {

    private static JsBridgeConfigImpl config = JsBridgeConfigImpl.getInstance();

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
            // 为JsModule设置context 和 WebView
            try {
                Method setContextMethod = module.getClass().getMethod("setContext", Context.class);
                setContextMethod.setAccessible(true);
                if (setContextMethod != null) {
                    setContextMethod.invoke(null, webView.getContext());
                }
                Method setWebViewMethod = module.getClass().getMethod("setWebView", WebView.class);
                setWebViewMethod.setAccessible(true);
                if (setWebViewMethod != null) {
                    setWebViewMethod.invoke(null, webView);
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
     * @param uriString
     * @param result
     */
    public static void callJsPrompt(String uriString, JsPromptResult result) {
        if (result == null) {
            throw new NullPointerException("JsPromptResult must not null");
        }
        if (TextUtils.isEmpty(uriString)) {
            throw new NullPointerException("uriString must not null");
        }
        JBArgumentParser argumentParser = JSON.parseObject(uriString, JBArgumentParser.class);
        if (argumentParser != null) {
            HashMap<String, JsMethod> methodHashMap = config.getExposedMethods().get(argumentParser.getModule());
            if (methodHashMap != null && !methodHashMap.isEmpty() && methodHashMap.containsKey(
                    argumentParser.getMethod())) {
                JsMethod method = methodHashMap.get(argumentParser.getMethod());
                try {
                    Object ret = method.invoke("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        result.confirm();
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
