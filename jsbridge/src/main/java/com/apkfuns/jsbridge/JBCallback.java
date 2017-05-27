package com.apkfuns.jsbridge;

import java.util.HashMap;

/**
 * Created by pengwei on 2017/5/27.
 */

public class JBCallback {

    public void apply(Object... callbackValue) {

    }

    public static void main(String[] args) {
        new JBCallback().apply(1.21f, 2, "", new String[]{}, new HashMap<>());
    }
}
