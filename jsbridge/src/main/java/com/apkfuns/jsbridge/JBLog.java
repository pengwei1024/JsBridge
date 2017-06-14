package com.apkfuns.jsbridge;

import android.util.Log;

/**
 * Created by pengwei on 2017/6/14.
 */

class JBLog {
    private static JsBridgeConfigImpl config = JsBridgeConfigImpl.getInstance();

    public static void d(String msg) {
        if (config.isDebug()) {
            Log.d(JsBridge.TAG, msg);
        }
    }

    public static void e(String msg, Throwable throwable) {
        if (config.isDebug()) {
            Log.e(JsBridge.TAG, msg, throwable);
        }
    }
}
