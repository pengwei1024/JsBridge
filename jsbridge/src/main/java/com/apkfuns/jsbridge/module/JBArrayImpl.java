package com.apkfuns.jsbridge.module;

import com.alibaba.fastjson.JSON;
import com.apkfuns.jsbridge.JBUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengwei on 2017/6/3.
 */

class JBArrayImpl extends WritableJBArray {

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
    public long getLong(int index) {
        return (long) get(index);
    }

    @Override
    public String getString(int index) {
        Object obj = get(index);
        if (obj == null || obj instanceof String) {
            return (String) obj;
        }
        return obj.toString();
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
    public JBCallback getCallback(int index) {
        return (JBCallback) get(index);
    }

    @Override
    public
    @JSArgumentType.Type
    int getType(int index) {
        if (get(index) != null) {
            return JBUtils.transformType(get(index).getClass());
        }
        return JSArgumentType.TYPE_UNDEFINE;
    }

    @Override
    public Object get(int index) {
        return dataSource.get(index);
    }

    private void add(Object data) {
        dataSource.add(data);
    }

    @Override
    public void pushNull() {
        dataSource.add(null);
    }

    @Override
    public void pushBoolean(boolean value) {
        add(value);
    }

    @Override
    public void pushDouble(double value) {
        add(value);
    }

    @Override
    public void pushInt(int value) {
        add(value);
    }

    @Override
    public void pushLong(long value) {
        add(value);
    }

    @Override
    public void pushString(String value) {
        add(value);
    }

    @Override
    public void pushArray(WritableJBArray value) {
        add(value);
    }

    @Override
    public void pushMap(WritableJBMap value) {
        add(value);
    }

    @Override
    public void pushCallback(JBCallback value) {
        add(value);
    }

    @Override
    public String convertJS() {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < dataSource.size(); i++) {
            builder.append(JBUtils.toJsObject(dataSource.get(i)));
            if (i != dataSource.size() - 1) {
                builder.append(",");
            }
        }
        return builder.append("]").toString();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(dataSource);
    }
}
