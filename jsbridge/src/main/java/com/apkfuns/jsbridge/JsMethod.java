package com.apkfuns.jsbridge;

import java.lang.reflect.Method;

/**
 * Created by pengwei on 16/5/11.
 */
public class JsMethod {
    private boolean isAsync;
    private Method javaMethod;

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }

    public void setJavaMethod(Method javaMethod) {
        this.javaMethod = javaMethod;
    }

    public static JsMethod create(boolean needCallback, Method method) {
        JsMethod jsMethod = new JsMethod();
        jsMethod.setAsync(needCallback);
        jsMethod.setJavaMethod(method);
        return jsMethod;
    }
}
