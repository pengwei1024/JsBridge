package com.apkfuns.jsbridge;

/**
 * Created by pengwei on 16/5/13.
 */
abstract class JsRunMethod {

    protected abstract String executeJS();

    public abstract String methodName();

    protected boolean isPrivate() {
        return true;
    }

    public final String getMethod() {
        StringBuilder builder = new StringBuilder();
        if (isPrivate()) {
            builder.append("function " + methodName());
        } else {
            builder.append("this." + methodName() + "=function");
        }
        String exec = executeJS();
        if (!exec.trim().endsWith(";")) {
            exec += ";";
        }
        builder.append(exec);
        return builder.toString();
    }
}
