package com.apkfuns.jsbridge;

import android.text.TextUtils;

/**
 * Created by pengwei on 16/5/13.
 */
abstract class JsRunMethod {

    private String jsMethodCache;

    protected abstract String executeJS();

    public abstract String methodName();

    protected boolean isPrivate() {
        return true;
    }

    /**
     * 是否启用缓存, 默认启用
     *
     * @return true=启用缓存
     */
    protected boolean enableCache() {
        return true;
    }

    public final String getMethod() {
        if (enableCache() && !TextUtils.isEmpty(jsMethodCache)) {
            return jsMethodCache;
        }
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
        jsMethodCache = builder.toString();
        return jsMethodCache;
    }
}
