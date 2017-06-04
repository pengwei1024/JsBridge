package com.apkfuns.jsbridge;

import com.apkfuns.jsbridge.util.JBArray;
import com.apkfuns.jsbridge.util.JBMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengwei on 2017/6/3.
 */

public class JBArrayImpl implements JBArray {

    private List<Object> dataSource;

    JBArrayImpl() {
        dataSource = new ArrayList<>();
    }

    @Override
    public int size() {
        return dataSource.size();
    }

    @Override
    public boolean isEmpty() {
        return dataSource.isEmpty();
    }

    @Override
    public boolean isNull(int index) {
        return get(index) == null;
    }

    @Override
    public boolean getBoolean(int index) {
        return (boolean) get(index);
    }

    @Override
    public double getDouble(int index) {
        return (double) get(index);
    }

    @Override
    public int getInt(int index) {
        return (int) get(index);
    }

    @Override
    public String getString(int index) {
        return (String) get(index);
    }

    @Override
    public JBMap getMap(int index) {
        return (JBMap) get(index);
    }

    @Override
    public JBArray getArray(int index) {
        return (JBArray) get(index);
    }

    @Override
    public
    @JSArgumentType.Type
    int getType(int index) {
        if (get(index) != null) {
            return Utils.transformType(get(index).getClass());
        }
        return JSArgumentType.TYPE_UNDEFINE;
    }

    @Override
    public Object get(int index) {
        return dataSource.get(index);
    }

    public void add(Object data) {
        dataSource.add(data);
    }
}
