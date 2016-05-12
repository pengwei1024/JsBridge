package com.apkfuns.jsbridge;

import java.lang.reflect.Method;

/**
 * Created by pengwei on 16/5/11.
 */
public class JsMethod {
    private boolean isAsync;
    private Method javaMethod;
    private String moduleName;

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

    public String getFunction() {
        return String.format("%s.%s.%s", JSBridge.getSchema(), moduleName, javaMethod.getName());
    }

    public String getCallbackFunction() {
        if (isAsync()) {
            return getFunction() + "Callback";
        }
        return null;
    }

    public static JsMethod create(boolean needCallback, Method method) {
        JsMethod jsMethod = new JsMethod();
        jsMethod.setAsync(needCallback);
        jsMethod.setJavaMethod(method);
        return jsMethod;
    }
}
