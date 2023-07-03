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
    private Map headers;

    public LoggerBean create(Map m) {
        this.setService(m.get(ILoggerBean.SERVICE).toString());

        Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
        Map<String, Object> jsonpayload = gson.fromJson(m.get(ILoggerBean.JSONPAYLOAD).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());

        this.setJsonpayload(jsonpayload);

        if(m.containsKey(ILoggerBean.HEADERS)) {
            Map<String, Object> headers = gson.fromJson(m.get(ILoggerBean.HEADERS).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
            this.setHeaders(headers);
        }

        return this;
    }
}
