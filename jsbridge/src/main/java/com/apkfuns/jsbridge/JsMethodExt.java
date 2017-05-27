package com.apkfuns.jsbridge;

import android.app.Activity;
import android.webkit.JsPromptResult;

/**
 * Created by pengwei on 2017/1/4.
 */

public abstract class JsMethodExt extends JsMethod {

    protected abstract String moduleName();

    protected abstract String getJsMethod();

    @Override
    public String getMethodName() {
        return getJsMethod();
    }

    @Override
    public String getModuleName() {
        return moduleName();
    }

    protected abstract void resultHandle(Activity activity, String params, JsReturn jsReturn);

    public final void handleResult(JsPromptResult result, Activity activity, String params) {
        if (result == null) {
            return;
        }
        JsReturn jsReturn = new JsReturn(result);
        resultHandle(activity, params, jsReturn);
    }
}
