package com.apkfuns.jsbridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.apkfuns.jsbridge.module.JSArgumentType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengwei on 2017/5/28.
 */

final class JBArgumentParser {
    private long id;
    private String module;
    private String method;
    private List<Parameter> parameters;
    private Throwable throwable;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @NonNull
    public String getErrorMsg() {
        return throwable == null? "Unknown Error": throwable.getMessage();
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * 解析是否成功
     * @return bool
     */
    public boolean isSuccess() {
        return throwable == null;
    }

    public static class Parameter {
        @JSArgumentType.Type
        private int type;
        private String name;
        private String value;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 解析为对象
     *
     * @param jsonString 前端传递的参数
     * @return JBArgumentParser
     */
    @NonNull
    static JBArgumentParser parse(@Nullable String jsonString) {
        JBArgumentParser parser = new JBArgumentParser();
        if(TextUtils.isEmpty(jsonString) || (!jsonString.startsWith("{") &&
                !jsonString.startsWith("["))) {
            parser.setThrowable(new JSONException("Argument error: " + jsonString));
            return parser;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            parser.setId(jsonObject.optLong("id"));
            parser.setMethod(jsonObject.optString("method"));
            parser.setModule(jsonObject.optString("module"));
            List<Parameter> parameterList = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("parameters");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.optJSONObject(i);
                    if (item == null) {
                        continue;
                    }
                    Parameter parameter = new Parameter();
                    if (item.has("name")) {
                        parameter.setName(item.optString("name"));
                    }
                    if (item.has("type")) {
                        parameter.setType(item.optInt("type"));
                    }
                    if (item.has("value")) {
                        parameter.setValue(item.optString("value"));
                    }
                    parameterList.add(parameter);
                }
            }
            parser.setParameters(parameterList);
        } catch (Exception e) {
            parser.setThrowable(e);
            JBLog.e("JBArgumentParser::parse Exception", e);
        }
        return parser;
    }
}
