package com.apkfuns.jsbridge.module;



import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by pengwei on 2017/5/28.
 */

public interface JSArgumentType {
    int TYPE_UNDEFINE = 0;
    int TYPE_STRING = 1;
    int TYPE_NUMBER = 2;
    int TYPE_BOOL = 3;
    int TYPE_FUNCTION = 4;
    int TYPE_OBJECT = 5;
    int TYPE_ARRAY = 6;
    int TYPE_INT = 7;
    int TYPE_FLOAT = 8;
    int TYPE_DOUBLE = 9;
    int TYPE_LONG = 10;

    @IntDef({TYPE_UNDEFINE, TYPE_STRING, TYPE_NUMBER, TYPE_BOOL, TYPE_FUNCTION, TYPE_OBJECT, TYPE_ARRAY,
            TYPE_INT, TYPE_FLOAT, TYPE_DOUBLE, TYPE_LONG})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }
}
