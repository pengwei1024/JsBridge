package com.apkfuns.jsbridge.module;


/**
 * Created by pengwei on 2017/6/10.
 */

public abstract class WritableJBMap implements JBMap {

    public abstract void putNull(String key);

    public abstract void putBoolean(String key, boolean value);

    public abstract void putDouble(String key, double value);

    public abstract void putInt(String key, int value);

    public abstract void putLong(String key, long value);

    public abstract void putString(String key, String value);

    public abstract void putArray(String key, WritableJBArray value);

    public abstract void putMap(String key, WritableJBMap value);

    public abstract void putCallback(String key, JBCallback value);

    public static WritableJBMap create() {
        return new JBMapImpl();
    }
}
