package com.apkfuns.jsbridgesample.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apkfuns.jsbridge.JsBridge;

/**
 * Created by pengwei on 2017/6/11.
 */

public class WebViewActivity extends BaseActivity {
    private JsBridge jsBridge;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("System WebView");
        jsBridge = JsBridge.loadModule();
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);
        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            url = "file:///android_asset/index.html";
        }
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if (jsBridge.callJsPrompt(message, result)) {
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(JsBridge.TAG, consoleMessage.message());
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(JsBridge.TAG, "start load JsBridge");
                jsBridge.injectJs(view);
            }
        });
    }

    @Override
    protected void onDestroy() {
        jsBridge.release();
        super.onDestroy();
    }
}
