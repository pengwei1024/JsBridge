package com.apkfuns.jsbridge;

import android.app.Activity;
import android.webkit.WebView;

/**
 * Created by pengwei on 16/5/14.
 */
public class ParameterType {

    /**
     * 1.1  string option
     * 2.1 option、jsCallback
     * 2.2 Activity options
     * 2.3  webView options
     * 3.1  activity,option,jsCallback
     * 3.2 webView, option, jsCallback
     * 3.3 activity,webView, option
     * 4.1 activity,webView,option, jsCallback
     */

    public static final int TYPE_O = 11;
    public static final int TYPE_OJ = 21;
    public static final int TYPE_AO = 22;
    public static final int TYPE_WO = 23;
    public static final int TYPE_AOJ = 31;
    public static final int TYPE_WOJ = 32;
    public static final int TYPE_AWO = 33;
    public static final int TYPE_AWOJ = 41;


    public static int getParameterType(Class[] parameters) {
        if (parameters != null && parameters.length > 0) {
            switch (parameters.length) {
                case 1:
                    if (Utils.classIsRelative(parameters[0], String.class)) {
                        return TYPE_O;
                    }
                    break;
                case 2:
                    if (Utils.classIsRelative(parameters[0], String.class) && Utils.classIsRelative(parameters[1], JSCallback.class)) {
                        return TYPE_OJ;
                    } else if (Utils.classIsRelative(parameters[0], Activity.class) && Utils.classIsRelative(parameters[1], String.class)) {
                        return TYPE_AO;
                    } else if (Utils.classIsRelative(parameters[0], WebView.class) && Utils.classIsRelative(parameters[1], String.class)) {
                        return TYPE_WO;
                    }
                    break;
                case 3:
                    if (Utils.classIsRelative(parameters[0], Activity.class) && Utils.classIsRelative(parameters[1], String.class) && Utils.classIsRelative(parameters[2], JSCallback.class)) {
                        return TYPE_AOJ;
                    } else if (Utils.classIsRelative(parameters[0], WebView.class) && Utils.classIsRelative(parameters[1], String.class) && Utils.classIsRelative(parameters[2], JSCallback.class)) {
                        return TYPE_WOJ;
                    } else if (Utils.classIsRelative(parameters[0], Activity.class) && Utils.classIsRelative(parameters[1], WebView.class) && Utils.classIsRelative(parameters[2], String.class)) {
                        return TYPE_AWO;
                    }
                    break;
                case 4:
                    if (Utils.classIsRelative(parameters[0], Activity.class) && Utils.classIsRelative(parameters[1], WebView.class)
                            && Utils.classIsRelative(parameters[2], String.class) && Utils.classIsRelative(parameters[3], JSCallback.class)) {
                        return TYPE_AWOJ;
                    }
                    break;
            }
        }
        return -1;
    }
}
