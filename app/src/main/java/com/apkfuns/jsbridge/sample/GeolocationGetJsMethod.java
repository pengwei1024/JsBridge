package com.apkfuns.jsbridge.sample;


import android.webkit.JsPromptResult;

public class GeolocationGetJsMethod extends JsMethod {

    @Override
    public String getPlatform() {
        return "device";
    }

    @Override
    public String getModule() {
        return "geolocation";
    }

    @Override
    public String getJsMethod() {
        return "get";
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
        StringBuffer js = new StringBuffer();
        JsMethodListener jsMethodListener = getJsMethodListener();
        if (jsMethodListener != null) {
            js.append(jsMethodListener.getAssignmentStatement("options.listener")).append(";");
        }
        return js.toString();
    }

    @Override
    public String getJsToJavaParamName() {
        return null;
    }

    @Override
    public JsMethodListener getJsMethodListener() {
        return GeolocationJsMethodListener.get();
    }

    @Override
    public void javaHandle(JsPromptResult result, String data) {

    }

}
