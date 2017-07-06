package com.apkfuns.jsbridge.module;

/**
 * Created by pengwei on 2017/5/28.
 */

public interface JBArray extends JsObject {
    int size();

    boolean isEmpty();

    boolean isNull(int index);

    Boolean getBoolean(int index);

    Double getDouble(int index);

    int getInt(int index);

    Long getLong(int index);

    String getString(int index);

    JBMap getMap(int index);

    JBArray getArray(int index);

    JBCallback getCallback(int index);

    int getType(int index);

    Object get(int index);
}
