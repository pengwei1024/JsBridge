package com.apkfuns.jsbridge.sample;

import android.webkit.JsPromptResult;

public interface JsMethodHandler {
    public void handle(JsPromptResult result, String data);
}
