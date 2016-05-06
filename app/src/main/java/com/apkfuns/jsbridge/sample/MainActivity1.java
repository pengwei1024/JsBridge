package com.apkfuns.jsbridge.sample;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.apkfuns.jsbridge.JSBridge;


public class MainActivity1 extends ActionBarActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSBridge.register(ServiceBridgeMethods.class);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);

        webView.getSettings().setAppCacheEnabled(true);
//        postEvaluateJs("var cc = 'ababa';localStorage.setItem('abc','***abc***')");
        webView.loadUrl("file:///android_asset/index3.html");
//        webView.addJavascriptInterface(new JsBridge(), "jsBridge");
        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
//                                      JsPromptResult result) {
//                final String callback = defaultValue;
//                final boolean useAsync = !TextUtils.isEmpty(callback);
//                if ("abc".equals(message)) {
//                    postEvaluateJs("(" + callback + ")(\"1234\")");
//                    return true;
//                }
//                return false;
//            }


            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                result.confirm(JSBridge.onJsPrompt(webView, message));
                return true;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                boolean consumed = super.onConsoleMessage(consoleMessage);
                if (!consumed) {
                    Log.d("WebView", consoleMessage.message());
                }
                return consumed;
            }
        });
    }

    class JsBridge {
        @JavascriptInterface
        public void alert(int msg) {

        }

        @JavascriptInterface
        public String getUserName() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "pengwei08";
        }

        @JavascriptInterface
        public void showImage(String imageUrl) {
            // 在这里可以执行加载图片的功能
            Toast.makeText(MainActivity1.this, imageUrl, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void asyncGetUserName(final String func) {
            new AsyncTask<Object, Void, String>() {
                @Override
                protected String doInBackground(Object... params) {
                    return "pengwei08++";
                }

                @Override
                protected void onPostExecute(String aVoid) {
                    super.onPostExecute(aVoid);
                    // 回调传过来的function
                    //String js = "var callback = " + func + "; callback('" + aVoid + "')";
                    //webView.loadUrl("javascript:(function(){" + js + "})()");
                    postEvaluateJs("(" + func + ")(\"" + aVoid + "\")");

                    // 回调已经存在的a()方法
                    // webView.loadUrl("javascript:a('"+aVoid+"')()");
                }
            }.execute();
        }
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
