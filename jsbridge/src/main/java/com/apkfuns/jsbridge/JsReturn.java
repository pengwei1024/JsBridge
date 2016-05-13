package com.apkfuns.jsbridge;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pengwei on 16/5/13.
 */
public class JsReturn {

    /**
     * 返回成功
     *
     * @param data
     * @return
     */
    public static String appleSuccess(String data) {
        JSONObject res = new JSONObject();
        try {
            res.put("onSuccess", 1);
            res.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    /**
     * 返回失败
     * @param errorMsg
     * @return
     */
    public static String appleFailure(String errorMsg) {
        JSONObject res = new JSONObject();
        try {
            res.put("onFailure", 1);
            res.put("errorMsg", errorMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

}
