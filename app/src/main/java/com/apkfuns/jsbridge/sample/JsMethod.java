package com.apkfuns.jsbridge.sample;


import android.webkit.JsPromptResult;

public abstract class JsMethod {

    public final static String JAVA_METHOD_PREFIX = "baiduhi";

    public abstract String getPlatform();

    public abstract String getModule();

    public abstract String getJsMethod();

    public String getJavaMethod() {
        return getPlatform() + "_" + getModule() + "_" + getJsMethod();
    }

    public abstract boolean hasResult();

    public abstract boolean hasJsToJavaParam();

    public abstract String getJsToJavaParamString();

    public abstract String getJsToJavaParamName();

    public abstract JsMethodListener getJsMethodListener();

    public void handle(JsPromptResult result, String data) {
        if (canBeHandle(data)) {
            jsMethodHandler.handle(result, data);
        }
    }

    public void handle(JsPromptResult result, String data, JsMethodHandler jsHandler) {
        if (canBeHandle(data)) {
            if (data != null && data.length() > 0) {
                String pre = JAVA_METHOD_PREFIX + "," + getJavaMethod();
                if (data.startsWith(pre + ",")) {
                    data = data.replaceFirst(pre + ",", "");
                } else if (data.startsWith(pre)) {
                    data = data.replaceFirst(pre, "");
                }
                if (jsHandler != null) {
                    jsHandler.handle(result, data.replace("&comma;", ",").replace("&amp;", "&").replace("\\\"", "\""));
                } else {
                    jsMethodHandler.handle(result,
                            data.replace("&comma;", ",").replace("&amp;", "&").replace("\\\"", "\""));
                }
            }
        }
    }

    public boolean canBeHandle(String data) {
        if (data == null || data.length() <= 0) {
            return false;
        }

        return data.startsWith(JAVA_METHOD_PREFIX + "," + getJavaMethod());
    }

    private JsMethodHandler jsMethodHandler = new JsMethodHandler() {
        @Override
        public void handle(JsPromptResult result, String data) {
            javaHandle(result, data);
        }
    };

    public abstract void javaHandle(JsPromptResult result, String data);

    public String getJs() {
        if (getPlatform() == null || getPlatform().length() == 0) {
            throw new IllegalArgumentException("Platform is null");
        }
        if (getModule() == null || getModule().length() == 0) {
            throw new IllegalArgumentException("Module is null");
        }
        if (getJsMethod() == null || getJsMethod().length() == 0) {
            throw new IllegalArgumentException("JsMethod is null");
        }
        if (getJavaMethod() == null || getJavaMethod().length() == 0) {
            throw new IllegalArgumentException("JavaMethod is null");
        }
        if (hasJsToJavaParam()) {
            if (getJsToJavaParamName() == null || getJsToJavaParamName().length() == 0) {
                throw new IllegalArgumentException("JsToJavaParamName is null");
            }
            if (getJsToJavaParamString() == null || getJsToJavaParamString().length() == 0) {
                throw new IllegalArgumentException("JsToJavaParamString is null");
            }
        }

        StringBuffer jsBuffer = new StringBuffer();
        jsBuffer.append(getJsMethod()).append(":function(");
        if (hasResult()) {
            jsBuffer.append("options){").append("if(options");
            if (hasJsToJavaParam()) {
                jsBuffer.append("&&options.").append(getJsToJavaParamName());
            }
            jsBuffer.append("){");
        } else {
            jsBuffer.append(getJsToJavaParamName()).append("){");
            if (hasJsToJavaParam()) {
                jsBuffer.append("if(").append(getJsToJavaParamName()).append("){");
                ;
            }
        }

        if (hasJsToJavaParam() || getJsMethodListener() != null) {
            jsBuffer.append("var param = \"\";");
            jsBuffer.append(getJsToJavaParamString());
        }

        if (hasResult()) {
            jsBuffer.append("var dataResult = ");
        }
        jsBuffer.append("window.prompt(\"").append(JAVA_METHOD_PREFIX).append(",").append(getJavaMethod());
        if (hasJsToJavaParam()) {
            jsBuffer.append(",\"+").append("param")
                    .append(".replace(/\\\"/g,\"\\\\\\\"\").replace(/&/g,\"&amp;\").replace(/,/g,\"&comma;\")")
                    .append("+\"");
        }
        jsBuffer.append("\");");
        if (hasResult()) {
            jsBuffer.append("var data=eval('('+dataResult+')');");
            jsBuffer.append(
                    "if(data&&data.onsuccess){options.onsuccess(data.data);}else if(data&&data.onfail){options.onfail(data.err);}");
        }
        if (hasResult()) {
            jsBuffer.append("}");
        } else {
            if (hasJsToJavaParam()) {
                jsBuffer.append("}");
            }
        }
        jsBuffer.append("}");
        return jsBuffer.toString();
    }
}
