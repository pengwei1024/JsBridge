package com.apkfuns.jsbridgesample.util;

import android.graphics.Bitmap;

/**
 * Created by pengwei on 2017/8/5.
 */

public abstract class TakePhotoResult {
    public abstract void onSuccess(Bitmap bitmap);

    public void onFailure(String error) {

    }
}
