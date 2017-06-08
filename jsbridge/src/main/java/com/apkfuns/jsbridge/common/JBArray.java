package com.apkfuns.jsbridge.common;

/**
 * Created by pengwei on 2017/5/28.
 */

public interface JBArray {
    int size();

    boolean isEmpty();

    boolean isNull(int index);

    boolean getBoolean(int index);

    double getDouble(int index);

    int getInt(int index);

    String getString(int index);

    JBMap getMap(int index);

    JBArray getArray(int index);

    int getType(int index);

    Object get(int index);
}
