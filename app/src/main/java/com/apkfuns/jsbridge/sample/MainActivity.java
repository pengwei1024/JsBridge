package com.apkfuns.jsbridge.sample;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apkfuns.jsbridge.JSBridge;


public class MainActivity extends ActionBarActivity {
    private WebView webView;


    private Menu menu;

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
//                JsMethod method = JsMethod.get(message);
//                if (method != null) {
//                    Log.wtf("ABC", method.getModuleAndMethod());
//                    if (method.getModuleAndMethod().equals("service.setTitleBackground")) {
//                        Log.wtf("abc", method.getParams() + "***" + message);
//                        setTitle(method.getParams());
//                    }
//                }
                result.confirm(JSBridge.callJsPrompt(MainActivity.this, webView, message));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public void addMenu(String title, final Runnable r) {
        if (menu != null) {
            menu.clear();
            MenuItem item = menu.add(Menu.NONE, 0, Menu.NONE, title).setIcon(R.mipmap.ic_launcher);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    r.run();
                    return true;
                }
            });
            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getTitle().equals("啊啊啊")) {
//            Toast.makeText(MainActivity.this, "aaa", Toast.LENGTH_SHORT).show();
//        }
        return super.onOptionsItemSelected(item);
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
