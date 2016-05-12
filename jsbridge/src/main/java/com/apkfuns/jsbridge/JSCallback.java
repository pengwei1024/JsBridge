package com.apkfuns.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by pengwei on 16/5/6.
 */
public class JSCallback {

    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static final String CALLBACK_JS_FORMAT = "javascript:JSBridge.%s('%s', %s);";
    private String mPort;
    private WeakReference<WebView> mWebViewRef;

    public JSCallback(WebView view, String port) {
        mWebViewRef = new WeakReference<>(view);
        mPort = port;
    }

    public JSCallback(WebView view, String url, int a) {
        mWebViewRef = new WeakReference<>(view);
    }

    private void apply(String func, JSONObject jsonObject) {
        final String execJs = String.format(CALLBACK_JS_FORMAT, func, mPort, String.valueOf(jsonObject));
        if (mWebViewRef != null && mWebViewRef.get() != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebViewRef.get().loadUrl(execJs);
                }
            });
        }
    }

    public void applyError(JSONObject jsonObject) {
        apply("onFailure", jsonObject);
    }

    public void applySuccess(JSONObject jsonObject) {
        apply("onFinish", jsonObject);
    }
}
