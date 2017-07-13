package com.apkfuns.jsbridge.module;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebView;

import com.apkfuns.jsbridge.JBUtils;
import com.apkfuns.jsbridge.common.IWebView;

/**
 * Created by pengwei on 2017/7/9.
 */

public abstract class JsListenerModule extends JsModule {

    /**
     * 执行 JS 回调方法
     * @param callPath
     * @param webView
     * @param args
     */
    protected static final void callJsListener(@NonNull String callPath, @NonNull WebView webView,
                                        @Nullable Object...args) {
        if (TextUtils.isEmpty(callPath) || webView == null) {
            return;
        }
        JBUtils.callJsMethod(callPath, webView, args);
    }

    /**
     * 执行 JS 回调方法
     * @param callPath
     * @param webView
     * @param args
     */
    protected static final void callJsListener(@NonNull String callPath, @NonNull IWebView webView,
                                        @Nullable Object...args) {
        if (TextUtils.isEmpty(callPath) || webView == null) {
            return;
        }
        JBUtils.callJsMethod(callPath, webView, args);
    }
}
