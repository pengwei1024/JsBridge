package com.apkfuns.jsbridge;

import com.apkfuns.jsbridge.module.JsModule;
import com.apkfuns.jsbridge.module.JsStaticModule;

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
    private boolean isStatic;
    private String protocol;

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
        this.isStatic = module instanceof JsStaticModule;
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getCallback() {
        if (isStatic) {
            return String.format("%s.%sCallback", getProtocol(),
                    getMethodName());
        }
        return String.format("%s.%s.%sCallback", getProtocol(),
                getModule().getModuleName(), getMethodName());
    }

    /**
     * 注入的JS代码
     *
     * @return
     */
    public String getInjectJs() {
        StringBuilder builder = new StringBuilder();
        if (isStatic) {
            builder.append("this." + getMethodName() + " = function(){");
        } else {
            builder.append(getMethodName() + ":function(){");
        }
        builder.append("try{");
        builder.append("var id=_getID(),args=[];");
        builder.append("if(!" + getCallback() + ")" + getCallback() + "={};");
        builder.append("for(var i in arguments){");
        builder.append("var name=id+'_a'+i,item=arguments[i],listeners={};");
        builder.append("var listeners = {};");
        builder.append("_parseFunction(item, name, listeners);");
        builder.append("for (var key in listeners) {");
        builder.append(getCallback() + "[key]=listeners[key];");
        builder.append("};");
        builder.append("args.push({type: _getType(item), name: name, value: item})");
        builder.append("}");
        builder.append("var ret =_callJava(id,'" + getModule().getModuleName() + "','"
                + getMethodName() + "', args);");
        builder.append("if(ret && ret.success) {");
        if (hasReturn) {
            builder.append("return ret.msg;");
        }
        builder.append("}else{");
        builder.append("console.error(ret.msg)}");
        builder.append("}catch(e){console.error(e);};");
        builder.append("}");
        if (!isStatic) {
            builder.append(",");
        } else {
            builder.append(";");
        }
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
                                  List<Integer> parameterTypeList, boolean hasReturn, String protocol) {
        JsMethod method = new JsMethod();
        method.setModule(jsModule);
        method.setJavaMethod(javaMethod);
        method.setMethodName(methodName);
        method.setParameterType(parameterTypeList);
        method.setHasReturn(hasReturn);
        method.setProtocol(protocol);
        return method;
    }
}
