package com.apkfuns.jsbridge;


import android.text.TextUtils;

import com.apkfuns.jsbridge.annotation.JSBridgeMethod;
import com.apkfuns.jsbridge.util.JBArray;
import com.apkfuns.jsbridge.util.JBMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pengwei on 16/5/13.
 */
final class Utils {

    private static List<Class> validParameterList =
            Arrays.<Class>asList(Integer.class, Float.class, Double.class, String.class,
                    Boolean.class, JSCallback.class, JBMap.class, JBArray.class,
                    int.class, float.class, double.class, boolean.class);

    /**
     * 获取类的静态公开方法
     *
     * @param injectedCls
     * @return
     * @throws Exception
     */
    public static HashMap<String, JsMethod> getAllMethod(JsModule module, Class injectedCls) throws Exception {
        HashMap<String, JsMethod> mMethodsMap = new HashMap<>();
        Method[] methods = injectedCls.getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return mMethodsMap;
        }
        for (Method javaMethod : methods) {
            String name = javaMethod.getName();
            int modifiers = javaMethod.getModifiers();
            if (TextUtils.isEmpty(name) || Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }
            JSBridgeMethod annotation = javaMethod.getAnnotation(JSBridgeMethod.class);
            if (annotation == null) {
                continue;
            }
            if (!TextUtils.isEmpty(annotation.methodName())) {
                name = annotation.methodName();
            }
            boolean hasReturn = !Void.class.equals(javaMethod.getReturnType());
            Class[] parameters = javaMethod.getParameterTypes();
            List<Integer> parameterTypeList = new ArrayList<>();
            // TODO: 2017/5/30 获取 method 参数名称 javassist
            for (Class cls : parameters) {
                if (!parameterIsValid(cls)) {
                    throw new IllegalArgumentException("Method " + javaMethod.getName() + " parameter is not Valid");
                }
                parameterTypeList.add(transformType(cls));
            }
            JsMethod createMethod = JsMethod.create(module, javaMethod, name,
                    parameterTypeList, hasReturn);
            mMethodsMap.put(name, createMethod);
        }
        return mMethodsMap;
    }

    /**
     * 是否是派生类
     *
     * @param cla1
     * @param cla2
     * @return
     */
    public static boolean classIsRelative(Class cla1, Class cla2) {
        if (cla1 != null && cla2 != null) {
            return cla2.isAssignableFrom(cla1) || cla2.equals(cla1);
        }
        return false;
    }

    /**
     * 参数是否匹配
     *
     * @param jsType
     * @param nativeObject
     * @return
     */
    public static boolean isParameterMatch(@JSArgumentType.Type int jsType, Object nativeObject) {
        if (nativeObject != null) {
            switch (jsType) {
                case JSArgumentType.TYPE_OBJECT:
                    return nativeObject instanceof JBMap;
                case JSArgumentType.TYPE_BOOL:
                    return nativeObject instanceof Boolean;
                case JSArgumentType.TYPE_NUMBER:
                    return nativeObject instanceof Integer || nativeObject instanceof Float
                            || nativeObject instanceof Double;
            }
        }
        // Parameter don't match, expect JBMap, actual string
        return false;
    }

    /**
     * native 注册方法参数是否符合要求
     * @param cls
     * @return
     */
    public static boolean parameterIsValid(Class cls) {
        return validParameterList.contains(cls);
    }

    /**
     * 将native参数映射到js
     * @param cls
     * @return
     */
    public static int transformType(Class cls) {
        if (cls.equals(Integer.class) || cls.equals(Float.class) || cls.equals(Double.class)) {
            return JSArgumentType.TYPE_NUMBER;
        } else if (cls.equals(Boolean.class)) {
            return JSArgumentType.TYPE_BOOL;
        } else if (cls.equals(String.class)) {
            return JSArgumentType.TYPE_STRING;
        } else if (cls.equals(JBArray.class)) {
            return JSArgumentType.TYPE_ARRAY;
        }else if (cls.equals(JBMap.class)) {
            return JSArgumentType.TYPE_OBJECT;
        }else if (cls.equals(JSCallback.class)) {
            return JSArgumentType.TYPE_FUNCTION;
        }
        return JSArgumentType.TYPE_UNDEFINE;
    }
}
