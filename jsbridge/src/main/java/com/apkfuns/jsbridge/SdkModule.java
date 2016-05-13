package com.apkfuns.jsbridge;

import android.webkit.WebView;


/**
 * Created by pengwei on 16/5/13.
 */
public class SdkModule implements JsModule {
    @Override
    public String getModuleName() {
        return "sdk";
    }

    public static int getVersion(WebView webView, String param) {
        return JsBridgeConfigImpl.getInstance().getSdkVersionCode();
    }
}
