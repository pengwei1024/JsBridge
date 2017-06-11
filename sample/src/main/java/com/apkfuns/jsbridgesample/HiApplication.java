package com.apkfuns.jsbridgesample;

import android.app.Application;

import com.apkfuns.jsbridge.JsBridgeConfig;
import com.apkfuns.jsbridgesample.module.NativeModule;
import com.apkfuns.jsbridgesample.module.ServiceModule;
import com.apkfuns.jsbridgesample.module.StaticModule;

/**
 * Created by pengwei on 2017/6/8.
 */

public class HiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JsBridgeConfig.getSetting().setProtocol("MyBridge").registerDefaultModule(
                ServiceModule.class, StaticModule.class, NativeModule.class).debugMode(true);
    }
}
