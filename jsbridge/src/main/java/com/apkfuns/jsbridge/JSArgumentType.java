package com.apkfuns.jsbridge;

import com.apkfuns.jsbridge.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by pengwei on 2017/5/28.
 */

public interface JSArgumentType {
    int TYPE_STRING = 0;
    int TYPE_NUMBER = 1;
    int TYPE_BOOL = 2;
    int TYPE_FUNCTION = 3;
    int TYPE_OBJECT = 4;
    int TYPE_ARRAY = 5;

    @IntDef({TYPE_STRING, TYPE_NUMBER, TYPE_BOOL, TYPE_FUNCTION, TYPE_OBJECT, TYPE_ARRAY})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }
}
