package com.apkfuns.jsbridge.module;

import com.apkfuns.jsbridge.JBUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by pengwei on 2017/5/30.
 */

class JBMapImpl extends WritableJBMap {

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
    public long getLong(String name) {
        return (long) get(name);
    }

    @Override
    public String getString(String name) {
        return (String) get(name);
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
        return dataSource.keySet();
    }

    private void put(String name, Object value) {
        dataSource.put(name, value);
    }

    @Override
    public void putNull(String key) {
        dataSource.put(key, null);
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
        StringBuilder builder = new StringBuilder("{");
        int i = 0;
        for (String key : dataSource.keySet()) {
            builder.append(key + ":" + JBUtils.toJsObject(get(key)));
            if (++i != dataSource.size()) {
                builder.append(",");
            }
        }
        return builder.append("}").toString();
    }
}
