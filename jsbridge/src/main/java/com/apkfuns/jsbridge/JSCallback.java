package com.apkfuns.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by pengwei on 16/5/6.
 */
public class JSCallback {

    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static final String CALLBACK_JS_FORMAT_VALUE = "javascript:%s('%s');";
    private static final String CALLBACK_JS_FORMAT = "javascript:%s();";

    private JsMethod method;
    private WeakReference<WebView> mWebViewRef;
    private String execJs;

    public JSCallback(WebView view, JsMethod method) {
        mWebViewRef = new WeakReference<>(view);
        this.method = method;
        execJs = String.format(CALLBACK_JS_FORMAT, method.getCallbackFunction());
    }

    public void apply(String callbackValue) {
        if (!TextUtils.isEmpty(callbackValue)) {
            execJs = String.format(CALLBACK_JS_FORMAT_VALUE, method.getCallbackFunction(), callbackValue);
        }
        if (mWebViewRef != null && mWebViewRef.get() != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebViewRef.get().loadUrl(execJs);
                }
            });
        }
    }

    public void apply() {
        apply(null);
    }

}
