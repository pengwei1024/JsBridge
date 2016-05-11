package com.apkfuns.jsbridge;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pengwei on 16/5/6.
 */
public class JSBridge {

    private static String schema;

    private static Map<String, HashMap<String, JsMethod>> exposedMethods = new HashMap<>();

    public static final String SCHEMA = "JSBridge";

    public static String getSchema() {
        return TextUtils.isEmpty(schema) ? SCHEMA : schema;
    }

    public static void setSchema(String schema) {
        JSBridge.schema = schema;
    }

    /**
     * 注册JS交互方法
     *
     * @param clazz
     */
    public static void register(Class<? extends JsModule> clazz) {
        try {
            JsModule type = clazz.newInstance();
            if (!exposedMethods.containsKey(type.getModuleName())) {
                exposedMethods.put(type.getModuleName(), getAllMethod(clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取类的所有static方法
     *
     * @param injectedCls
     * @return
     * @throws Exception
     */
    private static HashMap<String, JsMethod> getAllMethod(Class injectedCls) throws Exception {
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
                        if (parameters[0] == WebView.class && parameters[1] == JSONObject.class) {
                            mMethodsMap.put(name, JsMethod.create(false, method));
                        }
                        break;
                    case 3:
                        if (parameters[0] == WebView.class && parameters[1] == JSONObject.class && parameters[2] == JSCallback.class) {
                            mMethodsMap.put(name, JsMethod.create(true, method));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return mMethodsMap;
    }

    /**
     * 执行js回调
     *
     * @param webView
     * @param uriString
     * @return
     */
    public static String callJsPrompt(WebView webView, String uriString) {
        if (webView == null || TextUtils.isEmpty(uriString)) {
            return null;
        }
        Uri uri = Uri.parse(uriString);
        String methodName = "";
        String className = "";
        String param = "{}";
        String port = "";
        if (uriString.startsWith(getSchema())) {
            className = uri.getHost();
            param = uri.getQuery();
            port = uri.getPort() + "";
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                methodName = path.replace("/", "");
            }
        }
        if (exposedMethods.containsKey(className)) {
            HashMap<String, JsMethod> methodHashMap = exposedMethods.get(className);
            if (methodHashMap != null && methodHashMap.size() != 0 && methodHashMap.containsKey(methodName)) {
                JsMethod method = methodHashMap.get(methodName);
                if (method != null && method.getJavaMethod() != null) {
                    try {
                        if (!method.isAsync()) {
                            Object ret = method.getJavaMethod().invoke(null, webView, new JSONObject(param));
                            if (ret != null) {
                                return ret.toString();
                            }
                        } else {
                            method.getJavaMethod().invoke(null, webView, new JSONObject(param), new JSCallback(webView, port));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
