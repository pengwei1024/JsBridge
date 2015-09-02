package com.apkfuns.jsbridge.sample;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends ActionBarActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
        webView.addJavascriptInterface(new JsBridge(), "jsBridge");
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                boolean consumed = super.onConsoleMessage(consoleMessage);
                if (!consumed) {
                    Log.d("abab", consoleMessage.message());
                }
                return consumed;
            }
        });
    }

    class JsBridge {
        @JavascriptInterface
        public void alert(int msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("这是android弹出的提示框哟\n内容为:" + msg)
                    .setNegativeButton("确定", null)
                    .setPositiveButton("取消", null);
            builder.create().show();
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
        public void showImage(String imageUrl){
            // 在这里可以执行加载图片的功能
            Toast.makeText(MainActivity.this, imageUrl, Toast.LENGTH_LONG).show();
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
                    String js = "var callback = " + func + "; callback('" + aVoid + "')";
                    webView.loadUrl("javascript:(function(){" + js + "})()");

                    // 回调已经存在的a()方法
                    webView.loadUrl("javascript:a('"+aVoid+"')()");
                }
            }.execute();
        }
    }
}
