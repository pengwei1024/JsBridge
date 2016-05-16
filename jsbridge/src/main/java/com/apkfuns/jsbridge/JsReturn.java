package com.apkfuns.jsbridge;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pengwei on 16/5/13.
 */
public class JsReturn {

    private boolean success;
    private String data;

    public JsReturn(boolean success, String data) {
        this.success = success;
        this.data = data;
    }

    @Override
    public String toString() {
        JSONObject res = new JSONObject();
        try {
            if (success) {
                res.put("onSuccess", 1);
            } else {
                res.put("onFailure", 1);
            }
            res.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    /**
     * 返回成功
     *
     * @param data
     * @return
     */
    public static String appleSuccess(String data) {
        return new JsReturn(true, data).toString();
    }

    /**
     * 返回失败
     *
     * @param errorMsg
     * @return
     */
    public static String appleFailure(String errorMsg) {
        return new JsReturn(false, errorMsg).toString();
    }

}
