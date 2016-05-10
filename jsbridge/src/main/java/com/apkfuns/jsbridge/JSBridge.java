package com.apkfuns.jsbridge;

import android.net.Uri;
import android.text.TextUtils;
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

    private static Map<String, HashMap<String, Method>> exposedMethods = new HashMap<>();

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
    private static HashMap<String, Method> getAllMethod(Class injectedCls) throws Exception {
        HashMap<String, Method> mMethodsMap = new HashMap<>();
        Method[] methods = injectedCls.getDeclaredMethods();
        for (Method method : methods) {
            String name;
            if (method.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC) || (name = method.getName()) == null) {
                continue;
            }
            Class[] parameters = method.getParameterTypes();
            if (null != parameters && parameters.length == 3) {
                if (parameters[0] == WebView.class && parameters[1] == JSONObject.class && parameters[2] == JSCallback.class) {
                    mMethodsMap.put(name, method);
                }
            }
        }
        return mMethodsMap;
    }

    public static void d() {
        StringBuilder builder = new StringBuilder();
        builder.append("window." + getSchema() + " = {");
        for (String platform : exposedMethods.keySet()) {
            builder.append(platform + ":{");
            HashMap<String, Method> methods = exposedMethods.get(platform);
            for (String method : methods.keySet()) {
                
            }
            builder.append("},");
        }
        builder.append("};");
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
            HashMap<String, Method> methodHashMap = exposedMethods.get(className);
            if (methodHashMap != null && methodHashMap.size() != 0 && methodHashMap.containsKey(methodName)) {
                Method method = methodHashMap.get(methodName);
                if (method != null) {
                    try {
                        method.invoke(null, webView, new JSONObject(param), new JSCallback(webView, port));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
