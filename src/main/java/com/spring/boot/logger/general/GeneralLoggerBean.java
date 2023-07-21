package com.spring.boot.logger.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILoggerBean;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GeneralLoggerBean implements ILoggerBean {

    private final Integer logtype = GENERAL_LOG;
    private String service = AbstractLogger.getService();
    private Map jsonpayload;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map headers;

    public GeneralLoggerBean create(Map m) {
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
