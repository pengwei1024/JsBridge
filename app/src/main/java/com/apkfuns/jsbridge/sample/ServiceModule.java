package com.apkfuns.jsbridge.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

    /**
     * 设置标题栏颜色
     *
     * @param activity
     * @param param
     * @return
     */
    public static String setTitleBackground(MainActivity activity, String param) {
        activity.setTitleBackground(Color.parseColor(param));
        return JsReturn.appleSuccess("success");
    }

    /**
     * 增加右上角菜单
     *
     * @param activity
     * @param param
     * @param callback
     * @return
     */
    public static String addNavIcon(MainActivity activity, final String param, final JSCallback callback) {
        activity.addMenu(param, new Runnable() {
            @Override
            public void run() {
                callback.apply(param);
            }
        });
        return JsReturn.appleSuccess("success");
    }

    /**
     * 异步获取ticket
     *
     * @param object
     * @param callback
     */
    public static void getTicket(final String object, final JSCallback callback) {
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

    /**
     * 获取用户名
     *
     * @param option
     * @return
     */
    public static String getName(String option) {
        return JsReturn.appleSuccess("12345");
    }

    /**
     * 原生alert
     *
     * @param webView
     * @param option
     * @param callback
     * @return
     */
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

    public static String getLocation(MainActivity activity, String options, final JSCallback callback) {
        Location location = activity.getLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    callback.apply(String.format("%s,%s", location.getLatitude(), location.getLongitude()));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        if (location != null) {
            callback.apply(String.format("%s,%s", location.getLatitude(), location.getLongitude()));
            return JsReturn.appleSuccess("定位成功");
        }
        return JsReturn.appleFailure("定位失败");
    }

}
