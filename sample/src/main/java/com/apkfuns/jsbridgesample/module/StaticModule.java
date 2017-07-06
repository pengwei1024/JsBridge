package com.apkfuns.jsbridgesample.module;

import android.util.Log;
import android.widget.Toast;

import com.apkfuns.jsbridge.JsBridge;
import com.apkfuns.jsbridge.module.JBCallback;
import com.apkfuns.jsbridge.module.JBMap;
import com.apkfuns.jsbridge.module.JsStaticModule;
import com.apkfuns.jsbridge.module.JSBridgeMethod;

/**
 * Created by pengwei on 2017/6/8.
 */

public class StaticModule extends JsStaticModule {

    @JSBridgeMethod
    public void alert(String msg, JBCallback callback) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        callback.apply("123456");
    }

    @JSBridgeMethod
    public void mapToString(JBMap map) {
        Log.e(JsBridge.TAG, map.getString("data"));
    }
}
