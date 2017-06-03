package com.apkfuns.jsbridge;

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
}
