package com.spring.boot.logger.application.json;

import com.spring.boot.logger.AbstractLoggerBean;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.utils.InputValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Data
@EqualsAndHashCode(callSuper = true)
public class JsonLoggerBean extends AbstractLoggerBean implements ILoggerBean {

    private String url;
    private JSONObject response;
    private String message;
    private final Integer logtype = APPLICATION_LOG;
    private JSONObject headers;
    private String method;
    private String service;
    private JSONObject request;
    private String requestId;
    private Integer status;
    private String errorMessage;
    private String stacktrace;
    private String executiontime;

    public static JsonLoggerBean create(JSONObject json) throws ParseException {
        String message = "";
        String userAgent = "";
        JsonLoggerBean bean = new JsonLoggerBean();
        JSONParser parser = new JSONParser();
        bean.setService(json.get(ILoggerBean.SERVICE).toString());

        if (InputValidator.isNotNull(json.get(ILoggerBean.HEADERS))) {
            JSONObject headerJson = (JSONObject) parser.parse(json.get(ILoggerBean.HEADERS).toString());
            userAgent = headerJson.getOrDefault("user-agent", "").toString();
            bean.setHeaders(headerJson);
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.REQUEST_ID))) {
            bean.setRequestId(json.get(ILoggerBean.REQUEST_ID).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.REQUEST))) {
            JSONObject requestJson = (JSONObject) parser.parse(json.get(ILoggerBean.REQUEST).toString());
            bean.setRequest(requestJson);
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.RESPONSE))) {
            JSONObject responseJson = (JSONObject) parser.parse(json.get(ILoggerBean.RESPONSE).toString());
            bean.setResponse(responseJson);
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.METHOD))) {
            bean.setMethod(json.get(ILoggerBean.METHOD).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.URL))) {
            bean.setUrl(json.get(ILoggerBean.URL).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.STATUS))) {
            message = json.get(ILoggerBean.STATUS).toString();
            bean.setStatus(Integer.parseInt(json.get(ILoggerBean.STATUS).toString()));
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.EXECUTION_TIME))) {
            message = message + " " + json.get(ILoggerBean.EXECUTION_TIME).toString();
            bean.setExecutiontime(json.get(ILoggerBean.EXECUTION_TIME).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.MESSAGE))) {
            bean.setMessage(json.get(ILoggerBean.MESSAGE).toString());
        } else {
            message = bean.getMethod() + " " + bean.getUrl() + " " + message + " " + userAgent;
            bean.setMessage(message);
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.ERROR_MESSAGE))) {
            bean.setErrorMessage(json.get(ILoggerBean.ERROR_MESSAGE).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.STACKTRACE))) {
            bean.setStacktrace(json.get(ILoggerBean.STACKTRACE).toString());
        }

        return bean;
    }
}

