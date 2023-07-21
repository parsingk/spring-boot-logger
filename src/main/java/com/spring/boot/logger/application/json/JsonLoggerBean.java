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
public class JsonLoggerBean extends AbstractApplicationLoggerBean {

    private String url;
    private Map response;
    private String message;
//    private final Integer logtype = APPLICATION_LOG;
    private Map headers;
    private String method;
    private Map request;
    private String requestId;
    private Integer status;
    private String errorMessage;
    private String stacktrace;
    private String executiontime;

    public JsonLoggerBean create(Map m) throws ParseException {
        String message = "";
        String userAgent = "";
        Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
//        JSONParser parser = new JSONParser();

        if (InputValidator.isNotNull(m.get(ILoggerBean.HEADERS))) {
            Map<String, Object> headerJson = gson.fromJson(m.get(ILoggerBean.HEADERS).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
//            Map headerJson = (Map) parser.parse(m.get(ILoggerBean.HEADERS).toString());
            userAgent = headerJson.getOrDefault("user-agent", "").toString();
            this.setHeaders(headerJson);
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.REQUEST_ID))) {
            this.setRequestId(m.get(ILoggerBean.REQUEST_ID).toString());
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.REQUEST))) {
            Map<String, Object> requestJson = gson.fromJson(m.get(ILoggerBean.REQUEST).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
//            Map requestJson = (Map) parser.parse(m.get(ILoggerBean.REQUEST).toString());
            this.setRequest(requestJson);
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.RESPONSE))) {
            Map<String, Object> responseJson = gson.fromJson(m.get(ILoggerBean.RESPONSE).toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
//            Map responseJson = (Map) parser.parse(m.get(ILoggerBean.RESPONSE).toString());
            this.setResponse(responseJson);
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.METHOD))) {
            this.setMethod(m.get(ILoggerBean.METHOD).toString());
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.URL))) {
            this.setUrl(m.get(ILoggerBean.URL).toString());
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.STATUS))) {
            message = m.get(ILoggerBean.STATUS).toString();
            this.setStatus(Integer.parseInt(m.get(ILoggerBean.STATUS).toString()));
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.EXECUTION_TIME))) {
            message = message + " " + m.get(ILoggerBean.EXECUTION_TIME).toString();
            this.setExecutiontime(m.get(ILoggerBean.EXECUTION_TIME).toString());
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.MESSAGE))) {
            this.setMessage(m.get(ILoggerBean.MESSAGE).toString());
        } else {
            message = this.getMethod() + " " + this.getUrl() + " " + message + " " + userAgent;
            this.setMessage(message);
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.ERROR_MESSAGE))) {
            this.setErrorMessage(m.get(ILoggerBean.ERROR_MESSAGE).toString());
        }

        if (InputValidator.isNotNull(m.get(ILoggerBean.STACKTRACE))) {
            this.setStacktrace(m.get(ILoggerBean.STACKTRACE).toString());
        }

        return this;
    }
}

