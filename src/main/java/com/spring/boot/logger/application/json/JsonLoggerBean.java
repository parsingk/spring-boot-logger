package com.spring.boot.logger.application.json;

import com.spring.boot.logger.AbstractLoggerBean;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.utils.InputValidator;
import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Data
public class JsonLoggerBean extends AbstractLoggerBean implements ILoggerBean {

    private final Integer logtype = APPLICATION_LOG;
    private JSONObject headers;
    private String method;
    private String service;
    private String url;
    private JSONObject request;
    private JSONObject response;
    private Integer status;
    private String message;
    private String errorMessage;
    private String stacktrace;
    private String executiontime;

    public static JsonLoggerBean create(JSONObject json) throws ParseException {
        JsonLoggerBean bean = new JsonLoggerBean();
        JSONParser parser = new JSONParser();
        JSONObject headerJson = (JSONObject) parser.parse(json.get(ILoggerBean.HEADERS).toString());
        JSONObject requestJson = (JSONObject) parser.parse(json.get(ILoggerBean.REQUEST).toString());
        if (json.get(ILoggerBean.RESPONSE) != null) {
            JSONObject responseJson = (JSONObject) parser.parse(json.get(ILoggerBean.RESPONSE).toString());
            bean.setResponse(responseJson);
        }

        bean.setHeaders(headerJson);
        bean.setRequest(requestJson);
        bean.setMethod(json.get(ILoggerBean.METHOD).toString());
        bean.setService(json.get(ILoggerBean.SERVICE).toString());
        bean.setUrl(json.get(ILoggerBean.URL).toString());

        if (json.get(ILoggerBean.STATUS) != null) {
            bean.setStatus(Integer.parseInt(json.get(ILoggerBean.STATUS).toString()));
        }

        if (json.get(ILoggerBean.MESSAGE) != null) {
            bean.setMessage(json.get(ILoggerBean.MESSAGE).toString());
        }

        if (json.get(ILoggerBean.ERROR_MESSAGE) != null) {
            bean.setErrorMessage(json.get(ILoggerBean.ERROR_MESSAGE).toString());
        }

        if (json.get(ILoggerBean.STACKTRACE) != null) {
            bean.setStacktrace(json.get(ILoggerBean.STACKTRACE).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.EXECUTION_TIME))) {
            bean.setExecutiontime(json.get(ILoggerBean.EXECUTION_TIME).toString());
        }

        return bean;
    }
}

