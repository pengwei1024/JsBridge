package com.apkfuns.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;

import com.apkfuns.jsbridge.common.IWebView;
import com.apkfuns.jsbridge.module.JBCallback;

/**
 * Created by pengwei on 16/5/6.
 */
final class JBCallbackImpl implements JBCallback {

    private String name;
    private JsMethod method;
    private Handler mHandler;

    JBCallbackImpl(@NonNull JsMethod method, @NonNull String name) {
        this.method = method;
        this.name = name;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void apply(Object... args) {
        if (method == null || method.getModule() == null || method.getModule().mWebView == null
                || TextUtils.isEmpty(name)) {
            return;
        }
        String callback = method.getCallback();
        final StringBuilder builder = new StringBuilder("javascript:");
        builder.append("if(" + callback + " && " + callback + "['" + name + "']){");
        builder.append("var callback = " + callback + "['" + name + "'];");
        builder.append("if (typeof callback === 'function'){callback(");
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                builder.append(JBUtils.toJsObject(args[i]));
                if (i != args.length - 1) {
                    builder.append(",");
                }
            }
        }
        builder.append(")}else{console.error(callback + ' is not a function')}}");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (method.getModule().mWebView instanceof WebView) {
                    ((WebView) method.getModule().mWebView).loadUrl(builder.toString());
                } else if (method.getModule().mWebView instanceof IWebView) {
                    ((IWebView) method.getModule().mWebView).loadUrl(builder.toString());
                }
            }
        });
    }

}
