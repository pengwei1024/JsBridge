package com.apkfuns.jsbridgesample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.apkfuns.jsbridge.JsBridge;
import com.apkfuns.jsbridge.common.IPromptResult;
import com.apkfuns.jsbridge.common.IWebView;
import com.apkfuns.jsbridgesample.module.ListenerModule;

/**
 * Created by pengwei on 2017/6/11.
 */

public class CustomWebViewActivity extends BaseActivity implements WebEvent{
    private JsBridge jsBridge;
    private CustomWebView customWebView;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Custom WebView");
        jsBridge = JsBridge.loadModule();
        WebView.setWebContentsDebuggingEnabled(true);
        customWebView = new CustomWebView(this);
        setContentView(customWebView);
        customWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                jsBridge.injectJs(customWebView);
            }
        });
        customWebView.setPromptResult(new PromptResultCallback() {
            @Override
            public void onResult(String args, PromptResultImpl promptResult) {
                jsBridge.callJsPrompt(args, promptResult);
            }
        });
        customWebView.loadUrl("file:///android_asset/sample.html");
    }

    @Override
    public void takePhoto(TakePhotoResult result) {

    }

    public static class CustomWebView extends FrameLayout implements IWebView {

        private WebView webView;
        private PromptResultCallback callback;

        public CustomWebView(Context context) {
            super(context);
            this.webView = new WebView(context);
            this.webView.getSettings().setJavaScriptEnabled(true);
            addView(this.webView);
            this.webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsPrompt(WebView view, String url,
                                          String message, String defaultValue, JsPromptResult result) {
                    if (callback != null) {
                        callback.onResult(message, new PromptResultImpl(result));
                    }
                    return true;
                }

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    Log.d(JsBridge.TAG, consoleMessage.message());
                    return true;
                }
            });
        }

        public void setWebViewClient(WebViewClient webViewClient) {
            this.webView.setWebViewClient(webViewClient);
        }

        @Override
        public void loadUrl(String url) {
            webView.loadUrl(url);
        }

        public void setPromptResult(final PromptResultCallback callback) {
            this.callback = callback;
        }
    }

    public static class PromptResultImpl implements IPromptResult {
        private JsPromptResult jsPromptResult;

        public PromptResultImpl(JsPromptResult jsPromptResult) {
            this.jsPromptResult = jsPromptResult;
        }

        @Override
        public void confirm(String result) {
            this.jsPromptResult.confirm(result);
        }
    }

    public interface PromptResultCallback {
        void onResult(String args, PromptResultImpl promptResult);
    }

    @Override
    protected void onDestroy() {
        jsBridge.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListenerModule.onResume(customWebView, "hello");
    }

    @Override
    protected void onPause() {
        super.onPause();
        ListenerModule.onPause(customWebView, 1.234);
    }
}
