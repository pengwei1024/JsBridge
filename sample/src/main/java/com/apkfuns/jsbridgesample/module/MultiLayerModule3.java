package com.apkfuns.jsbridgesample.module;

import android.widget.Toast;

import com.apkfuns.jsbridge.module.JSBridgeMethod;
import com.apkfuns.jsbridge.module.JsModule;

/**
 * Created by pengwei on 2017/6/13.
 */

public class MultiLayerModule3 extends JsModule {
    @Override
    public String getModuleName() {
        return "a.b.c.d";
    }

    @JSBridgeMethod
    public void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
