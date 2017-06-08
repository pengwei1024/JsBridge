package com.apkfuns.jsbridge.common;

import android.content.Context;

/**
 * Created by pengwei on 2017/5/29.
 */

public interface IWebView {
    void loadUrl(String url);
    Context getContext();
}