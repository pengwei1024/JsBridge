package com.apkfuns.jsbridge;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apkfuns.jsbridge.module.JBCallback;
import com.apkfuns.jsbridge.module.JSArgumentType;
import com.apkfuns.jsbridge.module.JSBridgeMethod;
import com.apkfuns.jsbridge.common.JBArgumentErrorException;
import com.apkfuns.jsbridge.module.JBArray;
import com.apkfuns.jsbridge.module.JBMap;
import com.apkfuns.jsbridge.module.JsModule;
import com.apkfuns.jsbridge.module.JsObject;
import com.apkfuns.jsbridge.module.WritableJBArray;
import com.apkfuns.jsbridge.module.WritableJBMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pengwei on 16/5/13.
 */
public final class JBUtils {

    private static List<Class> validParameterList =
            Arrays.<Class>asList(Integer.class, Float.class, Double.class, String.class,
                    Boolean.class, JBCallback.class, JBMap.class, JBArray.class,
                    int.class, float.class, double.class, boolean.class);

    /**
     * 获取类的静态公开方法
     *
     * @param injectedCls
     * @return
     * @throws Exception
     */
    public static HashMap<String, JsMethod> getAllMethod(JsModule module, Class injectedCls, String protocol) throws Exception {
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
            for (Class cls : parameters) {
                if (!parameterIsValid(cls)) {
                    throw new IllegalArgumentException("Method " + javaMethod.getName() + " parameter is not Valid");
                }
                parameterTypeList.add(transformType(cls));
            }
            JsMethod createMethod = JsMethod.create(module, javaMethod, name,
                    parameterTypeList, hasReturn, protocol);
            mMethodsMap.put(name, createMethod);
        }
        return mMethodsMap;
    }

    /**
     * native 注册方法参数是否符合要求
     *
     * @param cls
     * @return
     */
    public static boolean parameterIsValid(Class cls) {
        return validParameterList.contains(cls);
    }

    /**
     * 将native参数映射到js
     *
     * @param cls
     * @return
     */
    public static
    @JSArgumentType.Type
    int transformType(Class cls) {
        if (cls.equals(Integer.class) || cls.equals(int.class)) {
            return JSArgumentType.TYPE_INT;
        } else if (cls.equals(Float.class) || cls.equals(float.class)) {
            return JSArgumentType.TYPE_FLOAT;
        } else if (cls.equals(Double.class) || cls.equals(double.class)) {
            return JSArgumentType.TYPE_DOUBLE;
        } else if (cls.equals(Long.class) || cls.equals(long.class)) {
            return JSArgumentType.TYPE_LONG;
        } else if (cls.equals(Boolean.class) || cls.equals(boolean.class)) {
            return JSArgumentType.TYPE_BOOL;
        } else if (cls.equals(String.class)) {
            return JSArgumentType.TYPE_STRING;
        } else if (cls.equals(JBArray.class)) {
            return JSArgumentType.TYPE_ARRAY;
        } else if (cls.equals(JBMap.class)) {
            return JSArgumentType.TYPE_OBJECT;
        } else if (cls.equals(JBCallback.class)) {
            return JSArgumentType.TYPE_FUNCTION;
        }
        return JSArgumentType.TYPE_UNDEFINE;
    }

    /**
     * 转换参数
     *
     * @param type
     * @param parameter
     * @return
     */
    public static Object parseToObject(@JSArgumentType.Type int type, JBArgumentParser.Parameter parameter,
                                       JsMethod method) {
        if (type == JSArgumentType.TYPE_INT || type == JSArgumentType.TYPE_FLOAT
                || type == JSArgumentType.TYPE_DOUBLE) {
            if (parameter.getType() != JSArgumentType.TYPE_NUMBER) {
                return new JBArgumentErrorException("parameter error, expect <number>");
            }
            try {
                switch (type) {
                    case JSArgumentType.TYPE_INT:
                        return Integer.parseInt(parameter.getValue());
                    case JSArgumentType.TYPE_FLOAT:
                        return Float.parseFloat(parameter.getValue());
                    case JSArgumentType.TYPE_DOUBLE:
                        return Double.parseDouble(parameter.getValue());
                    case JSArgumentType.TYPE_LONG:
                        return Long.parseLong(parameter.getValue());
                }
            } catch (NumberFormatException e) {
                return new JBArgumentErrorException(e.getMessage());
            }
        } else if (type == JSArgumentType.TYPE_STRING) {
            return parameter.getValue();
        } else if (type == JSArgumentType.TYPE_FUNCTION) {
            if (parameter.getType() != JSArgumentType.TYPE_FUNCTION) {
                return new JBArgumentErrorException("parameter error, expect <function>");
            }
            return new JBCallbackImpl(method, parameter.getName());
        } else if (type == JSArgumentType.TYPE_OBJECT) {
            if (parameter.getType() != JSArgumentType.TYPE_OBJECT) {
                return new JBArgumentErrorException("parameter error, expect <object>");
            }
            JSONObject jsonObject = JSON.parseObject(parameter.getValue());
            return parseObjectLoop(jsonObject, method);
        } else if (type == JSArgumentType.TYPE_ARRAY) {
            if (parameter.getType() != JSArgumentType.TYPE_ARRAY) {
                return new JBArgumentErrorException("parameter error, expect <array>");
            }
            JSONArray jsonArray = JSON.parseArray(parameter.getValue());
            return parseObjectLoop(jsonArray, method);
        }
        return null;
    }

    /**
     * parse object loop
     * @param parser
     * @param method
     * @return
     */
    private static Object parseObjectLoop(Object parser, JsMethod method) {
        if (parser != null) {
            if (parser instanceof BigDecimal) {
                return ((BigDecimal) parser).doubleValue();
            } else if (parser instanceof String) {
                String str = (String) parser;
                if (str.startsWith("[Function]::")) {
                    String[] function = str.split("::");
                    if (function.length == 2 && !TextUtils.isEmpty(function[1])) {
                        return new JBCallbackImpl(method, function[1]);
                    }
                }
            } else if (parser instanceof JSONObject) {
                WritableJBMap writableJBMap = new WritableJBMap.Create();
                JSONObject jsonObject = (JSONObject) parser;
                for (String key : jsonObject.keySet()) {
                    Object ret = parseObjectLoop(jsonObject.get(key), method);
                    if (ret == null) {
                        writableJBMap.putNull(key);
                        continue;
                    }
                    if (ret instanceof Integer) {
                        writableJBMap.putInt(key, (Integer) ret);
                    } else if (ret instanceof Double) {
                        writableJBMap.putDouble(key, (Double) ret);
                    } else if (ret instanceof Long) {
                        writableJBMap.putLong(key, (Long) ret);
                    } else if (ret instanceof String) {
                        writableJBMap.putString(key, (String) ret);
                    } else if (ret instanceof Boolean) {
                        writableJBMap.putBoolean(key, (Boolean) ret);
                    } else if (ret instanceof WritableJBArray) {
                        writableJBMap.putArray(key, (WritableJBArray) ret);
                    } else if (ret instanceof WritableJBMap) {
                        writableJBMap.putMap(key, (WritableJBMap) ret);
                    } else if (ret instanceof JBCallback) {
                        writableJBMap.putCallback(key, (JBCallback) ret);
                    } else {
                        writableJBMap.putNull(key);
                    }
                }
                return writableJBMap;
            } else if (parser instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) parser;
                WritableJBArray writableJBArray = new WritableJBArray.Create();
                for (int i = 0; i < jsonArray.size(); i++) {
                    Object ret = parseObjectLoop(jsonArray.get(i), method);
                    if (ret == null) {
                        writableJBArray.pushNull();
                        continue;
                    }
                    if (ret instanceof Integer) {
                        writableJBArray.pushInt((Integer) ret);
                    } else if (ret instanceof Double) {
                        writableJBArray.pushDouble((Double) ret);
                    } else if (ret instanceof Long) {
                        writableJBArray.pushLong((Long) ret);
                    } else if (ret instanceof String) {
                        writableJBArray.pushString((String) ret);
                    } else if (ret instanceof Boolean) {
                        writableJBArray.pushBoolean((Boolean) ret);
                    } else if (ret instanceof WritableJBArray) {
                        writableJBArray.pushArray((WritableJBArray) ret);
                    } else if (ret instanceof WritableJBMap) {
                        writableJBArray.pushMap((WritableJBMap) ret);
                    } else if (ret instanceof JBCallback) {
                        writableJBArray.pushCallback((JBCallback) ret);
                    } else {
                        writableJBArray.pushNull();
                    }
                }
                return writableJBArray;
            }
        }
        return parser;
    }

    /**
     * 转化为 JS 对象
     * @param javaObject
     * @return
     */
    public static String toJsObject(Object javaObject) {
        if (javaObject == null || javaObject instanceof Integer || javaObject instanceof Float
                || javaObject instanceof Double || javaObject instanceof Long
                || javaObject instanceof Boolean) {
            return "" + javaObject;
        } else if (javaObject instanceof JsObject) {
            return ((JsObject) javaObject).convertJS();
        } else {
            return "'" + javaObject + "'";
        }
    }

    public static List<String> moduleSplit(String moduleName) {
        List<String> moduleGroup = new ArrayList<>();
        int index = -1;
        do {
            int temp = moduleName.indexOf(".", index + 1);
            if (temp >= 0) {
                index = temp;
                moduleGroup.add(moduleName.substring(0, index));
            } else {
                if (index >= 0) {
                    moduleGroup.add(moduleName.substring(index + 1, moduleName.length()));
                } else {
                    moduleGroup.add(moduleName);
                }
                index = -1;
            }
        } while (index >= 0);
        return moduleGroup;
    }
}
