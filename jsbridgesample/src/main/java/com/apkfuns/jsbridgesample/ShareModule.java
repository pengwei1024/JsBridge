package com.apkfuns.jsbridgesample;

import android.widget.Toast;

import com.apkfuns.jsbridge.common.JSBridgeMethod;
import com.apkfuns.jsbridge.JBCallback;
import com.apkfuns.jsbridge.JsModule;
import com.apkfuns.jsbridge.common.JBMap;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by pengwei on 2017/5/27.
 */

public class ShareModule extends JsModule {
    @Override
    public String getModuleName() {
        return "share";
    }

    @JSBridgeMethod(methodName = "hiShare")
    public void share(float platform, String msg, final JBCallback success, final JBCallback failure) {
//        Log.d("****", platform + "#" + msg + "#" + success + "#" + failure);
//        Log.d("****", "context=" + getContext());
        Toast.makeText(getContext(), "abc", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        success.apply("ret = " + msg, true);
    }

    @JSBridgeMethod
    public void ajax(final JBMap dataMap) {
        OkHttpUtils.get()
                .url(dataMap.getString("url") + "?" + dataMap.getString("data"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dataMap.getJsCallback("error").apply(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        dataMap.getJsCallback("success").apply(response);
                    }
                });
        // String url, String type, String data, JBCallback success, JBCallback error
    }

//    @JSBridgeMethod
//    public void test(JBMap map) {
//        map.getJsCallback("a").apply("");
//    }
//
//    @JSBridgeMethod
//    public int version() {
//        return 10;
//    }
}
