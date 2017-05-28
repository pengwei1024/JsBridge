package com.apkfuns.jsbridge;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by pengwei on 16/5/6.
 */
public abstract class JsModule {
    private Context mContext;
    private WebView mWebView;

    protected final Context getContext() {
        return mContext;
    }

    private void setContext(Context context) {
        this.mContext = context;
    }

    public WebView getWebView() {
        return mWebView;
    }

    private void setWebView(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public abstract String getModuleName();
}

