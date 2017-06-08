package com.apkfuns.jsbridge;

import android.support.annotation.NonNull;
import android.webkit.JsPromptResult;
import android.webkit.WebView;

import com.apkfuns.jsbridge.common.IPromptResult;
import com.apkfuns.jsbridge.common.IWebView;

/**
 * Created by pengwei on 16/5/6.
 */
public final class JSBridge {

    private static final JsBridgeConfigImpl CONFIG = JsBridgeConfigImpl.getInstance();

    /**
     * inject JS for System WebView
     */
    public static void injectJs(@NonNull WebView webView) {
        if (webView == null) {
            return;
        }
        webView.loadUrl("javascript:" + CONFIG.getInjectJsString(webView.getContext(), webView));
    }

    /**
     * inject JS for Custom WebView
     * @param webView
     */
    public static void injectJs(@NonNull IWebView webView) {
        if (webView == null) {
            return;
        }
        webView.loadUrl("javascript:" + CONFIG.getInjectJsString(webView.getContext(), webView));
    }

    /**
     * call Native
     * @param methodArgs
     * @param result
     */
    public static void callJsPrompt(String methodArgs, JsPromptResult result) {
        CONFIG.callJsPrompt(methodArgs, result);
    }

    public static void callJsPrompt(String methodArgs, IPromptResult result) {
        CONFIG.callJsPrompt(methodArgs, result);
    }

    public static void release() {

    }
}
