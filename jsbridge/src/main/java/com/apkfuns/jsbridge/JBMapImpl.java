package com.apkfuns.jsbridge;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apkfuns.jsbridge.util.JBArray;
import com.apkfuns.jsbridge.util.JBMap;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by pengwei on 2017/5/30.
 */

class JBMapImpl implements JBMap {

    private HashMap<String, Object> dataSource;

    JBMapImpl() {
        this.dataSource = new HashMap<>();
    }

    @Override
    public boolean isEmpty() {
        return dataSource.isEmpty();
    }

    @Override
    public boolean hasKey(String name) {
        return dataSource.containsKey(name);
    }

    @Override
    public boolean isNull(String name) {
        return dataSource.get(name) == null;
    }

    @Override
    public Object get(String name) {
        return dataSource.get(name);
    }

    @Override
    public boolean getBoolean(String name) {
        return (boolean) get(name);
    }

    @Override
    public double getDouble(String name) {
        return (double) get(name);
    }

    @Override
    public int getInt(String name) {
        return (int) get(name);
    }

    @Override
    public String getString(String name) {
        return (String) get(name);
    }

    @Override
    public JBCallback getJsCallback(String name) {
        if (get(name) != null && get(name) instanceof JBCallback) {
            return ((JBCallback) get(name));
        }
        return null;
    }

    @Override
    public JBMap getJBMap(String name) {
        return null;
    }

    @Override
    public JBArray getJBArray(String name) {
        return null;
    }

    @Override
    public Set<String> keySet() {
        return dataSource.keySet();
    }

    public void put(String name, Object value) {
        dataSource.put(name, value);
    }

    public static JBMap create(String map, String callback, Object webView) {
        JSONObject jsonObject = JSON.parseObject(map);
        JBMapImpl jbMap = new JBMapImpl();
        if (jsonObject != null && !jsonObject.isEmpty()) {
            for (String key : jsonObject.keySet()) {
                Object child = jsonObject.get(key);
                if (child instanceof JSONObject) {

                } else if (child instanceof JSONArray) {

                } else if (child instanceof String) {
                    String stringParam = (String) child;
                    if (stringParam.startsWith("[Function]::")) {
                        String[] function = stringParam.split("::");
                        if (function.length == 2 && !TextUtils.isEmpty(function[1])) {
                            jbMap.put(key, new JBCallback(webView, callback, function[1]));
                        }
                    } else {
                        jbMap.put(key, stringParam);
                    }
                } else {
                    jbMap.put(key, child);
                }
            }
        }
        return jbMap;
    }
}
