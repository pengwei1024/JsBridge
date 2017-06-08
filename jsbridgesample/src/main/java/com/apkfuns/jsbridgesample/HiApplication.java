package com.apkfuns.jsbridgesample;

import android.app.Application;

import com.apkfuns.jsbridge.JsBridgeConfig;

/**
 * Created by pengwei on 2017/6/8.
 */

public class HiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JsBridgeConfig.getSetting().setProtocol("MyBridge").registerDefaultModule(
                ShareModule.class, StaticModule.class);
    }
}
