package com.apkfuns.jsbridge.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.apkfuns.jsbridge.JsPlatform;
import com.apkfuns.jsbridge.JSCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pengwei on 16/5/6.
 */
public class ServiceBridgeMethods implements JsPlatform {

    @Override
    public String getPlatform() {
        return "service";
    }

    public static void getTicket(WebView webView, JSONObject object, final JSCallback callback) {
        final JSONObject jsonObject = new JSONObject();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    jsonObject.put("ticket", "aaa+bcd");
                    callback.applySuccess(jsonObject);
                } catch (JSONException | InterruptedException e) {
                    callback.applyError(jsonObject);
                }
            }
        }).start();
    }

    public static void getData(WebView webView, JSONObject object, final JSCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return "abcdefg";
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    callback.applySuccess(new JSONObject().put("data", s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public static void alertDialog(WebView webView, JSONObject object, final JSCallback callback) {
        AlertDialog.Builder builder = null;
        try {
            builder = new AlertDialog.Builder(webView.getContext())
                    .setMessage("这是android弹出的提示框哟\n内容为:")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.applySuccess(null);
                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.applyError(null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.create().show();
    }
}
