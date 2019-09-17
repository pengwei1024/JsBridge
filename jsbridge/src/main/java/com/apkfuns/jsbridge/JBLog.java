package com.apkfuns.jsbridge;

import android.util.Log;

/**
 * Created by pengwei on 2017/6/14.
 */

class JBLog {
    // DEBUG
    private static final boolean DEBUG = JsBridgeConfigImpl.getInstance().isDebug();

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(JsBridge.TAG, msg);
        }
    }

    public static void e(String msg, Throwable throwable) {
        if (DEBUG) {
            Log.e(JsBridge.TAG, msg, throwable);
        }
    }
}
