package com.spring.boot.logger.general;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import com.spring.boot.logger.ILoggerBean;
import lombok.Data;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Data
public class LoggerBean implements ILoggerBean {

    private final Integer logtype = GENERAL_LOG;
    private String service;
    private Map jsonpayload;

    public static LoggerBean create(JSONObject json) {
        LoggerBean bean = new LoggerBean();
        bean.setService(json.get(ILoggerBean.SERVICE).toString());

        Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
        Map<String, Object> jsonpayload = gson.fromJson(json.get(ILoggerBean.JSONPAYLOAD).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());

        bean.setJsonpayload(jsonpayload);
        return bean;
    }
}
