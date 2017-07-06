package com.apkfuns.jsbridge.module;


/**
 * Created by pengwei on 2017/6/10.
 */

public interface WritableJBMap extends JBMap {

    void putNull(String key);

    void putBoolean(String key, boolean value);

    void putDouble(String key, double value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putString(String key, String value);

    void putArray(String key, WritableJBArray value);

    void putMap(String key, WritableJBMap value);

    void putCallback(String key, JBCallback value);

    class Create extends JBMapImpl {
        public Create() {}
    }
}
