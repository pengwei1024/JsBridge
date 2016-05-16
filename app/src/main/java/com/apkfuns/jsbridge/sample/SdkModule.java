package com.apkfuns.jsbridge.sample;

import android.webkit.WebView;

import com.apkfuns.jsbridge.JsModule;


/**
 * Created by pengwei on 16/5/13.
 */
public class SdkModule implements JsModule {
    @Override
    public String getModuleName() {
        return "sdk";
    }

    public static int getVersion(WebView webView, String param) {
        return 1;
    }
}
