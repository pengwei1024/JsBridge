package com.apkfuns.jsbridge.sample;

import com.apkfuns.jsbridge.JSCallback;
import com.apkfuns.jsbridge.JsModule;

/**
 * Created by pengwei on 16/5/15.
 */
public class NativeModule implements JsModule {
    @Override
    public String getModuleName() {
        return "native";
    }

    public static void setNavMenu(MainActivity activity, String options, final JSCallback callback) {
        activity.addMenu(options, new Runnable() {
            @Override
            public void run() {
                callback.apply();
            }
        });
    }
}
