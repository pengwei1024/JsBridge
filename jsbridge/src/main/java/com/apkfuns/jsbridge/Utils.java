package com.apkfuns.jsbridge;

import android.webkit.WebView;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Created by pengwei on 16/5/13.
 */
public class Utils {

    /**
     * 获取类的静态公开方法
     *
     * @param injectedCls
     * @return
     * @throws Exception
     */
    public static HashMap<String, JsMethod> getAllMethod(String module, Class injectedCls) throws Exception {
        HashMap<String, JsMethod> mMethodsMap = new HashMap<>();
        Method[] methods = injectedCls.getDeclaredMethods();
        for (Method method : methods) {
            String name;
            if (method.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC) || (name = method.getName()) == null) {
                continue;
            }
            Class[] parameters = method.getParameterTypes();
            if (null != parameters) {
                switch (parameters.length) {
                    case 2:
                        if (parameters[0] == WebView.class && parameters[1] == String.class) {
                            mMethodsMap.put(name, JsMethod.create(false, module, method));
                        }
                        break;
                    case 3:
                        if (parameters[0] == WebView.class && parameters[1] == String.class && parameters[2] == JSCallback.class) {
                            mMethodsMap.put(name, JsMethod.create(true, module, method));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return mMethodsMap;
    }
}
