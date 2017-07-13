package com.apkfuns.jsbridgesample.module;

import com.apkfuns.jsbridge.common.IWebView;
import com.apkfuns.jsbridge.module.JsListenerModule;

/**
 * Created by pengwei on 2017/7/9.
 */

public class ListenerModule extends JsListenerModule {
    @Override
    public String getModuleName() {
        return "page";
    }

    public static void onResume(IWebView webView, Object...args) {
        callJsListener("MyBridge.page.onResume", webView, args);
    }

    public static void onPause(IWebView webView, Object...args) {
        callJsListener("MyBridge.page.onPause", webView, args);
    }
}
