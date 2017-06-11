package com.apkfuns.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;

import com.apkfuns.jsbridge.common.IWebView;
import com.apkfuns.jsbridge.module.JBArray;
import com.apkfuns.jsbridge.module.JBMap;

import java.lang.ref.WeakReference;

/**
 * Created by pengwei on 16/5/6.
 */
public final class JBCallback {

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private String callback;
    private WeakReference<Object> mWebViewRef;
    private String name;

    JBCallback(Object webView, String callback, @NonNull String name) {
        this.callback = callback;
        this.name = name;
        mWebViewRef = new WeakReference<>(webView);
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setWebViewRef(Object webView) {
        this.mWebViewRef = new WeakReference<Object>(webView);
    }

    public void apply(Object... args) {
        if (mWebViewRef == null || mWebViewRef.get() == null || TextUtils.isEmpty(callback)
                || TextUtils.isEmpty(name)) {
            return;
        }
        final StringBuilder builder = new StringBuilder("javascript:");
        builder.append("if(" + callback + " && " + callback + "['" + name + "']){");
        builder.append("var callback = " + callback + "['" + name + "'];");
        builder.append("if (typeof callback === 'function'){callback(");
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                builder.append(Utils.toJsObject(args[i]));
                if (i != args.length - 1) {
                    builder.append(",");
                }
            }
        }
        builder.append(")}else{console.error(callback + ' is not a function')}}");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mWebViewRef.get() instanceof WebView) {
                    ((WebView) mWebViewRef.get()).loadUrl(builder.toString());
                } else if (mWebViewRef.get() instanceof IWebView) {
                    ((IWebView) mWebViewRef.get()).loadUrl(builder.toString());
                }
            }
        });
    }

}
