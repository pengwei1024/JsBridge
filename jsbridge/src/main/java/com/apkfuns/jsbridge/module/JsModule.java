package com.apkfuns.jsbridge.module;

import android.content.Context;
import android.webkit.WebView;

import com.apkfuns.jsbridge.common.IWebView;

/**
 * Created by pengwei on 16/5/6.
 */
public abstract class JsModule {
    public Context mContext;
    public Object mWebView;

    protected final Context getContext() {
        return mContext;
    }

    protected IWebView getIWebView() {
        return (IWebView) mWebView;
    }

    protected WebView getWebView() {
        return (WebView) mWebView;
    }

    protected Object getWebViewObject() {
        return mWebView;
    }

    public abstract String getModuleName();
}

