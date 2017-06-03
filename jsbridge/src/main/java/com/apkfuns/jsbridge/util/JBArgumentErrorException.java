package com.apkfuns.jsbridge.util;

/**
 * Created by pengwei on 2017/6/3.
 */

public class JBArgumentErrorException extends RuntimeException {

    public JBArgumentErrorException(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
