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

    public abstract boolean callJsPrompt(@NonNull String methodArgs, @NonNull JsPromptResult result);

    public abstract boolean callJsPrompt(@NonNull String methodArgs, @NonNull IPromptResult result);

    public abstract void evaluateJavascript(@NonNull String jsCode);

    public abstract void clean();

    public abstract void release();


    /**
     * Recommended Use loadModule(JsModule...modules)
     */
    @Deprecated
    public static JsBridge loadModule(Class<? extends JsModule>... modules) {
        JsModule[] newModule = null;
        if (modules != null) {
            newModule = new JsModule[modules.length];
            for (int i = 0; i < newModule.length; i++) {
                try {
                    newModule[i] = modules[i].newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new JsBridgeImpl(newModule);
    }

    public static JsBridge loadModule(JsModule...modules) {
        return new JsBridgeImpl(modules);
    }

    public static JsBridge loadModule() {
        return new JsBridgeImpl();
    }

    public static JsBridge loadModule(@NonNull String protocol, @NonNull String readyMethod, JsModule...modules) {
        return new JsBridgeImpl(protocol, readyMethod, modules);
    }
}
