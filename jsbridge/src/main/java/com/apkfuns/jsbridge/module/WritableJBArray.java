package com.apkfuns.jsbridge.module;


/**
 * Created by pengwei on 2017/6/10.
 */

public abstract class WritableJBArray implements JBArray {

    public abstract void pushNull();

    public abstract void pushBoolean(boolean value);

    public abstract void pushDouble(double value);

    public abstract void pushInt(int value);

    public abstract void pushLong(long value);

    public abstract void pushString(String value);

    public abstract void pushArray(WritableJBArray value);

    public abstract void pushMap(WritableJBMap value);

    public abstract void pushCallback(JBCallback value);

    public static WritableJBArray create() {
        return new JBArrayImpl();
    }
}
