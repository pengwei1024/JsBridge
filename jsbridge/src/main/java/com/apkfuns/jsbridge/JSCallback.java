package com.apkfuns.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by pengwei on 16/5/6.
 */
public class JSCallback {

    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static final String CALLBACK_JS_FORMAT_VALUE = "javascript:%s[%s]('%s');";
    private static final String CALLBACK_JS_FORMAT = "javascript:%s[%s]();";
    private static final String CLEAR_CALLBACK = "delete %s[%s];";

    private JsMethod method;
    private WeakReference<WebView> mWebViewRef;
    private String execJs;
    private String port;

    public JSCallback(WebView view, JsMethod method, String port) {
        this.port = port;
        mWebViewRef = new WeakReference<>(view);
        this.method = method;
        execJs = String.format(CALLBACK_JS_FORMAT, method.getCallbackFunction(), port);
    }

    public void apply(String callbackValue) {
        if (!TextUtils.isEmpty(callbackValue)) {
            execJs = String.format(CALLBACK_JS_FORMAT_VALUE, method.getCallbackFunction(), port, callbackValue);
        }
        if (mWebViewRef != null && mWebViewRef.get() != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO: 16/5/16 删除导致按钮点击无法回调
//                    execJs += String.format(CLEAR_CALLBACK, method.getCallbackFunction(), port);
                    mWebViewRef.get().loadUrl(execJs);
                }
            });
        }
    }

    public void apply() {
        apply(null);
    }

}
