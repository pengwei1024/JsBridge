package com.apkfuns.jsbridge;

import android.content.Context;

/**
 * Created by pengwei on 16/5/6.
 */
public abstract class JsModule {
    private Context context;

    protected final Context getContext() {
        return context;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public abstract String getModuleName();
}

