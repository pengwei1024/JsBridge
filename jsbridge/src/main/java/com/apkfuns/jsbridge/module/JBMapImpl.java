package com.apkfuns.jsbridge.module;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by pengwei on 2017/5/30.
 */

class JBMapImpl extends JSONObject implements WritableJBMap {

    JBMapImpl() {}

    @Override
    public boolean isEmpty() {
        return super.length() == 0;
    }

    @Override
    public boolean hasKey(String name) {
        return super.has(name);
    }

    @Override
    public boolean isNull(String name) {
        return super.isNull(name);
    }

    @Override
    public Object get(String name) {
        return super.opt(name);
    }

    @Override
    public boolean getBoolean(String name) {
        return optBoolean(name);
    }

    @Override
    public double getDouble(String name) {
        return optDouble(name);
    }

    @Override
    public int getInt(String name) {
        return optInt(name);
    }

    @Override
    public long getLong(String name) {
        return optLong(name);
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
        Set<String> sets = new HashSet<>();
        Iterator<String> iterator = super.keys();
        while (iterator.hasNext()) {
            sets.add(iterator.next());
        }
        return sets;
    }

    @Override
    public void putNull(String key) {
        putValue(key, null);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        putValue(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        putValue(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        putValue(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        putValue(key, value);
    }

    @Override
    public void putString(String key, String value) {
        putValue(key, value);
    }

    @Override
    public void putArray(String key, WritableJBArray value) {
        putValue(key, value);
    }

    @Override
    public void putMap(String key, WritableJBMap value) {
        putValue(key, value);
    }

    @Override
    public void putCallback(String key, JBCallback value) {
        putValue(key, value);
    }

    @Override
    public String convertJS() {
        return toString();
    }

    private void putValue(String key, Object value) {
        try {
            super.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
