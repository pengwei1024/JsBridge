package com.apkfuns.jsbridgesample;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.apkfuns.jsbridge.util.IWebView;

/**
 * Created by pengwei on 2017/5/29.
 */

public class CustomWebView implements IWebView {

    private WebView webView;

    public CustomWebView(WebView webView) {
        this.webView = webView;
        this.webView.getSettings().setJavaScriptEnabled(true);
    }

    public void setWebChromeClient(WebChromeClient chromeClient) {
        webView.setWebChromeClient(chromeClient);
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public Context getContext() {
        return webView.getContext();
    }
}
