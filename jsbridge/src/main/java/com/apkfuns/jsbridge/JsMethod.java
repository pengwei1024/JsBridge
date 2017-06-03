package com.apkfuns.jsbridge;

import android.net.Uri;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengwei on 16/5/11.
 */
class JsMethod {
    private Method javaMethod;
    private JsModule module;
    private String methodName;
    private boolean hasReturn;
    private List<Integer> parameterType = new ArrayList<>();

    protected JsMethod() {
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

    public JsModule getModule() {
        return module;
    }

    public void setModule(JsModule module) {
        this.module = module;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Integer> getParameterType() {
        return parameterType;
    }

    public void setParameterType(List<Integer> parameters) {
        this.parameterType = parameters;
    }

    public boolean hasReturn() {
        return hasReturn;
    }

    public void setHasReturn(boolean hasReturn) {
        this.hasReturn = hasReturn;
    }

    public String getCallback() {
        return String.format("%s.%s.%sCallback", JsBridgeConfigImpl.getInstance().getProtocol(),
                getModule().getModuleName(), getMethodName());
    }

    /**
     * 注入的JS代码
     *
     * @return
     */
    public String getInjectJs() {
        StringBuilder builder = new StringBuilder(getMethodName() + ":function(){");
        builder.append("try{");
        builder.append("var id = Math.floor(Math.random() * (1 << 30));");
        builder.append("var req = {id: id, module: '" + getModule().getModuleName()
                + "', method: '" + getMethodName() + "'};");
        builder.append("var argumentList = [], callbacks = [];");
        builder.append(getCallback() + "={};");
        builder.append("for (var i in arguments) {");
        builder.append("var name = 'a' + i; var item = arguments[i];");
        builder.append("var listeners = {};");
        builder.append("_parseFunction(item, name, listeners);");
        builder.append("for (var key in listeners) {");
        builder.append(getCallback() + "[key]=listeners[key];");
        builder.append("callbacks.push(key);");
        builder.append("};");
        builder.append("argumentList.push({type: _getType(item), name: name, value: item})");
        builder.append("}");
        builder.append("req['parameters'] = argumentList;");
        builder.append("req['callback'] = callbacks;");
        builder.append("console.log(JSON.stringify(req));");
        builder.append("var ret = JSON.parse(prompt(JSON.stringify(req)));");
        builder.append("if(ret && ret.success) {");
        if (hasReturn) {
            builder.append("return ret.msg;");
        }
        builder.append("}else{");
        builder.append("console.error(ret.msg)}");
        builder.append("}catch(e){console.error(e);};");
        builder.append("},");
        return builder.toString();
    }

    public Object invoke(Object... args) throws Exception {
        if (javaMethod != null) {
            javaMethod.setAccessible(true);
            return javaMethod.invoke(getModule(), args);
        }
        return null;
    }

    /**
     * 创建实例
     *
     * @param jsModule
     * @param javaMethod
     * @param methodName
     * @param parameterTypeList
     * @return
     */
    public static JsMethod create(JsModule jsModule, Method javaMethod, String methodName,
                                  List<Integer> parameterTypeList, boolean hasReturn) {
        JsMethod method = new JsMethod();
        method.setModule(jsModule);
        method.setJavaMethod(javaMethod);
        method.setMethodName(methodName);
        method.setParameterType(parameterTypeList);
        method.setHasReturn(hasReturn);
        return method;
    }
}
