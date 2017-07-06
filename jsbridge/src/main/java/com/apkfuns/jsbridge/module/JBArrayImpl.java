package com.apkfuns.jsbridge.module;

import com.alibaba.fastjson.JSONArray;
import com.apkfuns.jsbridge.JBUtils;


/**
 * Created by pengwei on 2017/6/3.
 */

class JBArrayImpl extends JSONArray implements WritableJBArray {

    JBArrayImpl() {}

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean isNull(int index) {
        return get(index) == null;
    }

    @Override
    public Boolean getBoolean(int index) {
        return (boolean) get(index);
    }

    @Override
    public Double getDouble(int index) {
        return (double) get(index);
    }

    @Override
    public int getInt(int index) {
        return (int) get(index);
    }

    @Override
    public Long getLong(int index) {
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
        return super.get(index);
    }

    @Override
    public void pushNull() {
        super.add(null);
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
        return toString();
    }
}
