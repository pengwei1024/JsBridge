package com.apkfuns.jsbridge.sample;

import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.webkit.WebView;

public abstract class JsMethodListener extends JsMethod {
    public final static String JAVA_METHOD_PREFIX = "baiduhi";

    public final static String JS_METHOD_PREFIX = "BdHiJs";

    @Override
    public boolean hasResult() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasJsToJavaParam() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getJsToJavaParamString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getJsToJavaParamName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsMethodListener getJsMethodListener() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void javaHandle(JsPromptResult result, String data) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getJs() {
        return getJsMethod() + ":null";
    }

    public String getJsListenerPrefix() {
        return JS_METHOD_PREFIX + "." + getPlatform() + "." + getModule() + "." + getJsMethod();
    }

    public String getAssignmentStatement(String value) {
        return getJsListenerPrefix() + "=" + value;
    }

    public String getFunctionStatement() {
        return getFunctionStatement((String) null);
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

    public String getFunctionStatement(String...value) {
        StringBuffer js = new StringBuffer();
        js.append("try{if(").append(getJsListenerPrefix()).append("&&typeof(").append(getJsListenerPrefix())
                .append(")==\"function\"){");
        if (value != null) {
            StringBuffer v = new StringBuffer();
            for (String val : value) {
                if (!TextUtils.isEmpty(val)) {
                    v = v.append("\"").append(val.replace("\"", "\\\"")).append("\",");
                }
            }
            int len = v.length();
            if (v.length() > 0) {
                len = v.length() - 1;
            }
            js.append(getJsListenerPrefix() + "(" + v.substring(0, len) + ");");
        } else {
            js.append(getJsListenerPrefix() + "();");
        }
        js.append("}}catch(e){}");
        return js.toString();
    }

    public void exeFunctionStatement(WebView mController, String value) {
        if (mController != null) {
            mController.loadUrl("javascript:" + getFunctionStatement(value) + ";");
        }
    }

    public void exeFunctionStatement(WebView mController, String...value) {
        if (mController != null) {
            mController.loadUrl("javascript:" + getFunctionStatement(value) + ";");
        }
    }
}