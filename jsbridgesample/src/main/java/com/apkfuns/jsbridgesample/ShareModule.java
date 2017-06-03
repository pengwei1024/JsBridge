package com.apkfuns.jsbridgesample;

import android.util.Log;
import android.widget.Toast;

import com.apkfuns.jsbridge.annotation.JSBridgeMethod;
import com.apkfuns.jsbridge.JBCallback;
import com.apkfuns.jsbridge.JsModule;

/**
 * Created by pengwei on 2017/5/27.
 */

public class ShareModule extends JsModule {
    @Override
    public String getModuleName() {
        return "share";
    }

    @JSBridgeMethod(methodName = "hiShare")
    public void share(float platform, String msg, JBCallback success, JBCallback failure) {
        Log.d("****", platform + "#" + msg + "#" + success + "#" + failure);
        Log.d("****", "context=" + getContext());
        Toast.makeText(getContext(), "abc", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(2000);
            success.apply("12345", 1.5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @JSBridgeMethod
//    public void test(JBMap map) {
//        map.getJsCallback("a").apply("");
//    }
//
//    @JSBridgeMethod
//    public int version() {
//        return 10;
//    }
}
