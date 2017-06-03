package com.apkfuns.jsbridge;

import android.content.Context;
import android.webkit.WebView;

import com.apkfuns.jsbridge.util.IWebView;

/**
 * Created by pengwei on 16/5/6.
 */
public abstract class JsModule {
    private Context mContext;
    private Object mWebView;

    protected final Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public IWebView getIWebView() {
        return (IWebView) mWebView;
    }

    public WebView getWebView() {
        return (WebView) mWebView;
    }

    protected Object getWebViewObject() {
        return mWebView;
    }

    public void setWebView(Object mWebView) {
        this.mWebView = mWebView;
    }

    public abstract String getModuleName();
}

