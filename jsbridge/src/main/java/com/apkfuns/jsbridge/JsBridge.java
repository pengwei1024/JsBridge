package com.apkfuns.jsbridge;

import android.support.annotation.NonNull;
import android.webkit.JsPromptResult;
import android.webkit.WebView;

import com.apkfuns.jsbridge.common.IPromptResult;
import com.apkfuns.jsbridge.common.IWebView;
import com.apkfuns.jsbridge.module.JsModule;

/**
 * Created by pengwei on 2017/6/8.
 */

public abstract class JsBridge {

    public static final String TAG = "JsBridgeDebug";

    public abstract void injectJs(@NonNull WebView webView);

    public abstract void injectJs(@NonNull IWebView webView);

    public abstract void callJsPrompt(@NonNull String methodArgs, @NonNull JsPromptResult result);

    public abstract void callJsPrompt(@NonNull String methodArgs, @NonNull IPromptResult result);

    public abstract void clean();

    public abstract void release();

    public static JsBridge loadModule(Class<? extends JsModule>... modules) {
        return new JsBridgeImpl(modules);
    }
}
