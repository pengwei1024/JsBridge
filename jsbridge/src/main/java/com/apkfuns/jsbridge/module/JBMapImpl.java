package com.apkfuns.jsbridge.module;

import com.alibaba.fastjson.JSONObject;


import java.util.Set;

/**
 * Created by pengwei on 2017/5/30.
 */

class JBMapImpl extends JSONObject implements WritableJBMap {

    JBMapImpl() {}

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean hasKey(String name) {
        return super.containsKey(name);
    }

    @Override
    public boolean isNull(String name) {
        return super.get(name) == null;
    }

    @Override
    public Object get(String name) {
        return super.get(name);
    }

    @Override
    public Boolean getBoolean(String name) {
        return (boolean) get(name);
    }

    @Override
    public Double getDouble(String name) {
        return (double) get(name);
    }

    @Override
    public int getInt(String name) {
        return (int) get(name);
    }

    @Override
    public Long getLong(String name) {
        return (long) get(name);
    }

    @Override
    public String getString(String name) {
        Object obj = get(name);
        if (obj == null || obj instanceof String) {
            return (String) obj;
        }
        return obj.toString();
    }

    @Override
    public JBCallback getCallback(String name) {
        if (get(name) != null && get(name) instanceof JBCallback) {
            return ((JBCallback) get(name));
        }
        return null;
    }

    @Override
    public JBMap getJBMap(String name) {
        return (JBMap) get(name);
    }

    @Override
    public JBArray getJBArray(String name) {
        return (JBArray) get(name);
    }

    @Override
    public Set<String> keySet() {
        return super.keySet();
    }

    @Override
    public void putNull(String key) {
        super.put(key, null);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        put(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        put(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        put(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        put(key, value);
    }

    @Override
    public void putString(String key, String value) {
        put(key, value);
    }

    @Override
    public void putArray(String key, WritableJBArray value) {
        put(key, value);
    }

    @Override
    public void putMap(String key, WritableJBMap value) {
        put(key, value);
    }

    @Override
    public void putCallback(String key, JBCallback value) {
        put(key, value);
    }

    @Override
    public String convertJS() {
        return toString();
    }
}
