package com.apkfuns.jsbridge.sample;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

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
        public String getUserName(){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "pengwei08";
        }
        @JavascriptInterface
        public String asyncGetUserName(){
            new AsyncTask<Object, Void, String>(){
                @Override
                protected String doInBackground(Object... params) {
                    return null;
                }
                @Override
                protected void onPostExecute(String aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute();
            return "pengwei08";
        }
    }
}
