package com.apkfuns.jsbridgesample.module;

import com.apkfuns.jsbridge.module.JBCallback;
import com.apkfuns.jsbridge.module.JSBridgeMethod;
import com.apkfuns.jsbridge.module.JsModule;

/**
 * Created by pengwei on 2017/6/15.
 */

public abstract class HiModule extends JsModule {

    @JSBridgeMethod
    public void startUpload(JBCallback callback) {

    }
}
