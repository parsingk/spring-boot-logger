package com.spring.boot.logger.application.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import com.spring.boot.logger.application.AbstractApplicationLoggerBean;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.utils.InputValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationLoggerBean extends AbstractApplicationLoggerBean {

    private String url;
    private String message;
//    private final Integer logtype = APPLICATION_LOG;
    private Map headers;
    private String method;
    private Map request;

    public ApplicationLoggerBean create(Map m) throws ParseException {
        String message = "";
        String userAgent = "";
        Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

//        this.setService(m.get(ILoggerBean.SERVICE).toString());

        if (InputValidator.isNotNull(m.get(ILoggerBean.HEADERS))) {
            Map<String, Object> headerJson = gson.fromJson(m.get(ILoggerBean.HEADERS).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
            userAgent = headerJson.getOrDefault("user-agent", "").toString();
            this.setHeaders(headerJson);
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.REQUEST))) {
            Map<String, Object> requestJson = gson.fromJson(m.get(ILoggerBean.REQUEST).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
            this.setRequest(requestJson);
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.METHOD))) {
            this.setMethod(m.get(ILoggerBean.METHOD).toString());
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.URL))) {
            this.setUrl(m.get(ILoggerBean.URL).toString());
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.MESSAGE))) {
            this.setMessage(m.get(ILoggerBean.MESSAGE).toString());
        } else {
            message = this.getMethod() + " " + this.getUrl() + " " + message + " " + userAgent;
            this.setMessage(message);
        }

        return this;
    }
}
