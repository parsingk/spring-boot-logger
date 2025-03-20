package com.spring.boot.logger.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.config.LoggerConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonPropertyOrder({"service", "logtype", "jsonpayload", "headers", "message", "stacktrace"})
public class GeneralLoggerBean implements ILoggerBean {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String timestamp;
    private String service = LoggerConfig.getService();
    private final Integer logtype = GENERAL_LOG;
    private Map jsonpayload;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map headers;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stacktrace;

    public GeneralLoggerBean create(Map m) {
        Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

        if(!m.containsKey(ILoggerBean.JSONPAYLOAD)) {
            throw new IllegalArgumentException("No Key about jsonpayload.");
        }

        Map<String, Object> jsonpayload = gson.fromJson(m.get(ILoggerBean.JSONPAYLOAD).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
        this.setJsonpayload(jsonpayload);

        if(m.containsKey(ILoggerBean.HEADERS)) {
            Map<String, Object> headers = gson.fromJson(m.get(ILoggerBean.HEADERS).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
            this.setHeaders(headers);
        }

        if(m.containsKey(ILoggerBean.MESSAGE)) {
            this.setMessage(m.get(ILoggerBean.MESSAGE).toString());
        }

        if(m.containsKey(ILoggerBean.STACKTRACE)) {
            this.setStacktrace(m.get(ILoggerBean.STACKTRACE).toString());
        }

        return this;
    }

    public void addTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
