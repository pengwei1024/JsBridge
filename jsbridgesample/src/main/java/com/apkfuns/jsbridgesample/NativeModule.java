package com.apkfuns.jsbridgesample;

import com.apkfuns.jsbridge.JsModule;

/**
 * Created by pengwei on 16/5/15.
 */
public class NativeModule extends JsModule {
    @Override
    public String getModuleName() {
        return null;
    }
//    @Override
//    public String getModuleName() {
//        // 这里设置模块名称
//        return "native";
//    }
//
//    @JSBridgeMethod(methodName = "show")
//    private void showToast(int a, Map<String, String> maps, JBCallback success, JBCallback failure) {
//
//    }
//
//    @JSBridgeMethod
//    public static void setNavMenu(MainActivity activity, WebView webView, final String options,
//                                  final JBCallback callback, JsReturn jsReturn) {
//        // 调用MainActivity的addMenu方法设置菜单，并将点击事件回调给JS
//        activity.addMenu(options, new Runnable() {
//            @Override
//            public void run() {
//                callback.apply(options);
//            }
//        });
//        jsReturn.onSuccess("12345");
//    }
}
