package com.apkfuns.jsbridge;

import com.apkfuns.jsbridge.module.JsModule;

/**
 * Created by pengwei on 16/5/13.
 */
public abstract class JsBridgeConfig {

    // 注册module
    public abstract JsBridgeConfig registerDefaultModule(Class<? extends JsModule>... modules);

    // 设置协议头
    public abstract JsBridgeConfig setProtocol(String protocol);

    // 加载结束函数名
    public abstract JsBridgeConfig setLoadReadyMethod(String readyName);

    // 调试模式
    public abstract JsBridgeConfig debugMode(boolean debug);

    public abstract String getProtocol();

    public static JsBridgeConfig getSetting() {
        return JsBridgeConfigImpl.getInstance();
    }
}
