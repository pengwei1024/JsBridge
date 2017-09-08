package com.apkfuns.jsbridge.module;

import com.apkfuns.jsbridge.JBUtils;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by pengwei on 2017/6/3.
 */

class JBArrayImpl extends JSONArray implements WritableJBArray {

    JBArrayImpl() {}

    @Override
    public int size() {
        return super.length();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean isNull(int index) {
        return get(index) == null;
    }

    @Override
    public boolean getBoolean(int index) {
        return optBoolean(index);
    }

    @Override
    public double getDouble(int index) {
        return optDouble(index);
    }

    @Override
    public int getInt(int index) {
        return optInt(index);
    }

    @Override
    public long getLong(int index) {
        return optLong(index);
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
        return super.opt(index);
    }

    @Override
    public void pushNull() {
        super.put(null);
    }

    @Override
    public void pushBoolean(boolean value) {
        put(value);
    }

    @Override
    public void pushDouble(double value) {
        try {
            put(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushInt(int value) {
        put(value);
    }

    @Override
    public void pushLong(long value) {
        put(value);
    }

    @Override
    public void pushString(String value) {
        put(value);
    }

    @Override
    public void pushArray(WritableJBArray value) {
        put(value);
    }

    @Override
    public void pushMap(WritableJBMap value) {
        put(value);
    }

    @Override
    public void pushCallback(JBCallback value) {
        put(value);
    }

    @Override
    public String convertJS() {
        return toString();
    }
}
