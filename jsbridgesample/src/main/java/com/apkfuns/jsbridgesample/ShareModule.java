package com.apkfuns.jsbridgesample;

import android.widget.Toast;

import com.apkfuns.jsbridge.JSBridgeMethod;
import com.apkfuns.jsbridge.JSCallback;
import com.apkfuns.jsbridge.JsModule;
import com.apkfuns.jsbridge.util.JBMap;

/**
 * Created by pengwei on 2017/5/27.
 */

public class ShareModule extends JsModule {
    @Override
    public String getModuleName() {
        return "share";
    }

    @JSBridgeMethod
    public void share(int platform, String msg, JSCallback success, JSCallback failure) {
        Toast.makeText(getContext(), "abc", Toast.LENGTH_SHORT).show();
    }

    @JSBridgeMethod
    public void test(JBMap map) {
        map.getJsCallback("a").apply("");
    }

    @JSBridgeMethod
    public int version() {
        return 10;
    }
}
