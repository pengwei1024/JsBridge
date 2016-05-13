package com.apkfuns.jsbridge;

import java.lang.reflect.Method;

/**
 * Created by pengwei on 16/5/11.
 */
public class JsMethod {
    private boolean needCallback;
    private Method javaMethod;
    private String moduleName;
    private String methodName;

    private JsMethod() {
    }

    public boolean needCallback() {
        return needCallback;
    }

    public void setNeedCallback(boolean needCallback) {
        this.needCallback = needCallback;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }

    public void setJavaMethod(Method javaMethod) {
        this.javaMethod = javaMethod;
        if (javaMethod != null) {
            methodName = javaMethod.getName();
        }
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFunction() {
        return String.format("%s.%s.%s", JsBridgeConfigImpl.getInstance().getProtocol(),
                getModuleName(), getMethodName());
    }

    public String getCallbackFunction() {
        if (needCallback()) {
            return getFunction() + "Callback";
        }
        return null;
    }

    /**
     * 注入的JS代码
     *
     * @return
     */
    public String getInjectJs() {
        StringBuilder builder = new StringBuilder(getMethodName() + ":function(option){");
        builder.append("if(option === undefined){return prompt('" + getUrl() + "');};");
        if (needCallback()) {
            builder.append("if(option.onListener && typeof(option.onListener) === 'function'){");
            builder.append(getCallbackFunction() + "=" + "option.onListener;");
            builder.append("}");
        }
        builder.append("var result = prompt('" + getUrl() + "' + (option.data ||''));");
        builder.append("if(result === null) return;");
        builder.append("var data = eval('(' + result + ')');");
        builder.append("if(data && data.onSuccess){option.onSuccess(data.data);}");
        builder.append("else if(data && data.onFailure){option.onFailure(data.errorMsg);}");
        builder.append("},");
        return builder.toString();
    }

    /**
     * 获取协议的url
     *
     * @return
     */
    public String getUrl() {
        return String.format("%s://%s:%d/%s?", JsBridgeConfigImpl.getInstance().getProtocol(),
                getModuleName(), 0, getMethodName());
    }

    public static JsMethod create(boolean needCallback, String moduleName, Method method) {
        JsMethod jsMethod = new JsMethod();
        jsMethod.setNeedCallback(needCallback);
        jsMethod.setJavaMethod(method);
        jsMethod.setModuleName(moduleName);
        return jsMethod;
    }
}
