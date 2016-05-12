package com.apkfuns.jsbridge.sample;

import android.webkit.JsPromptResult;

/**
 * Created by pengwei on 16/5/11.
 */
public class A extends JsMethod {
    @Override
    public String getPlatform() {
        return "android";
    }

    @Override
    public String getModule() {
        return "service";
    }

    @Override
    public String getJsMethod() {
        return "getName";
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public boolean hasJsToJavaParam() {
        return false;
    }

    @Override
    public String getJsToJavaParamString() {
        return "baiduHi.android.service.getName = options.listener;";
    }

    @Override
    public String getJsToJavaParamName() {
        return null;
    }

    @Override
    public JsMethodListener getJsMethodListener() {
        return null;
    }

    @Override
    public void javaHandle(JsPromptResult result, String data) {

    }

    public String getJsListenerPrefix() {
        return "baiduHi" + "." + getPlatform() + "." + getModule() + "." + getJsMethod();
    }

    public String getFunctionStatement(String value) {
        StringBuffer js = new StringBuffer();
        js.append("try{if(").append(getJsListenerPrefix()).append("&&typeof(").append(getJsListenerPrefix())
                .append(")==\"function\"){");
        if (value != null) {
            js.append(getJsListenerPrefix() + "(\"" + value.replace("\"", "\\\"") + "\");");
        } else {
            js.append(getJsListenerPrefix() + "();");
        }
        js.append("}}catch(e){}");
        return js.toString();
    }
}
