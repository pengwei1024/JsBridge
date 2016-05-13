package com.apkfuns.jsbridge.sample;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apkfuns.jsbridge.JSBridge;
import com.apkfuns.jsbridge.JsMethod;

public class MainActivity extends ActionBarActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSBridge.getConfig().setProtocol("DuLifeBridge").setSdkVersion(1).registerModule(ServiceModule.class);



        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAppCacheEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                JsMethod method = null;
                setTitle("123456");
                result.confirm(JSBridge.callJsPrompt(webView, message));
                return true;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                boolean consumed = super.onConsoleMessage(consoleMessage);
                if (!consumed) {
                    Log.wtf("WebView", consoleMessage.message());
                }
                return consumed;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                JSBridge.injectJs(webView);
            }
        });
    }

    private void postEvaluateJs(final String script) {
        if (script == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + script, null);
            }
        });
    }
}
