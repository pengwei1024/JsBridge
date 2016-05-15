package com.apkfuns.jsbridgesample;

import com.apkfuns.jsbridge.JSCallback;
import com.apkfuns.jsbridge.JsModule;

/**
 * Created by pengwei on 16/5/15.
 */
public class NativeModule implements JsModule {
    @Override
    public String getModuleName() {
        // 这里设置模块名称
        return "native";
    }

    public static void setNavMenu(MainActivity activity, final String options, final JSCallback callback) {
        // 调用MainActivity的addMenu方法设置菜单，并将点击事件回调给JS
        activity.addMenu(options, new Runnable() {
            @Override
            public void run() {
                callback.apply(options);
            }
        });
    }
}
