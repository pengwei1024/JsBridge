package com.apkfuns.jsbridge.module;

import java.util.Set;

/**
 * Created by pengwei on 2017/5/28.
 */

public interface JBMap extends JsObject {
    boolean isEmpty();

    boolean hasKey(String name);

    boolean isNull(String name);

    Object get(String name);

    Boolean getBoolean(String name);

    Double getDouble(String name);

    int getInt(String name);

    Long getLong(String name);

    String getString(String name);

    JBCallback getCallback(String name);

    JBMap getJBMap(String name);

    JBArray getJBArray(String name);

    Set<String> keySet();
}
