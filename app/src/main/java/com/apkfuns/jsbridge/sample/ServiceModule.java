package com.apkfuns.jsbridge.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.apkfuns.jsbridge.JsModule;
import com.apkfuns.jsbridge.JSCallback;
import com.apkfuns.jsbridge.JsReturn;

/**
 * Created by pengwei on 16/5/6.
 */
public class ServiceModule implements JsModule {

    @Override
    public String getModuleName() {
        return "service";
    }

    public static String setTitleBackground(MainActivity activity, String param) {
        activity.setTitleBackground(Color.parseColor(param));
        return JsReturn.appleSuccess("success");
    }

    public static String addNavIcon(MainActivity activity, final String param, final JSCallback callback) {
        activity.addMenu(param, new Runnable() {
            @Override
            public void run() {
                callback.apply(param);
            }
        });
        return JsReturn.appleSuccess("success");
    }

    public static void getTicket(WebView webView, final String object, final JSCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            protected void onPostExecute(String s) {
                callback.apply(s);
            }
        }.execute();
    }

    public static String getName(WebView webView, String option) {
        return JsReturn.appleSuccess("1234");
    }

    public static String alertDialog(WebView webView, String option, final JSCallback callback) {
        AlertDialog.Builder builder = null;
        try {
            builder = new AlertDialog.Builder(webView.getContext())
                    .setMessage("这是android弹出的提示框哟\n内容为:" + option)
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.apply("确定");
                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.apply("取消");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.create().show();
        return JsReturn.appleSuccess("aa");
    }

}
