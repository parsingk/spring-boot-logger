package com.spring.boot.logger.application.stackdriver;

import com.spring.boot.logger.AbstractLoggerBean;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.utils.InputValidator;
import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Data
public class StackdriverLoggerBean extends AbstractLoggerBean implements ILoggerBean {

    private final Integer logtype = APPLICATION_LOG;
    private JSONObject headers;
    private String method;
    private String service;
    private String url;
    private JSONObject request;
    private String requestId;
    private String executiontime;
    private Integer status;
    private String message;
    private String errorMessage;
    private String stacktrace;
    private JSONObject response;

    public static StackdriverLoggerBean create(JSONObject json) throws ParseException {
        String message = "";
        StackdriverLoggerBean bean = new StackdriverLoggerBean();
        JSONParser parser = new JSONParser();
        JSONObject headerJson = (JSONObject) parser.parse(json.get(ILoggerBean.HEADERS).toString());
        JSONObject requestJson = (JSONObject) parser.parse(json.get(ILoggerBean.REQUEST).toString());
        bean.setHeaders(headerJson);
        bean.setRequest(requestJson);

        bean.setRequestId(json.get(ILoggerBean.REQUEST_ID).toString());
        bean.setMethod(json.get(ILoggerBean.METHOD).toString());
        bean.setService(json.get(ILoggerBean.SERVICE).toString());
        bean.setUrl(json.get(ILoggerBean.URL).toString());

        if (InputValidator.isNotNull(json.get(ILoggerBean.EXECUTION_TIME))) {
            bean.setExecutiontime(json.get(ILoggerBean.EXECUTION_TIME).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.RESPONSE))) {
            JSONObject responseJson = (JSONObject) parser.parse(json.get(ILoggerBean.RESPONSE).toString());
            bean.setResponse(responseJson);
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.STATUS))) {
            message = json.get(ILoggerBean.STATUS).toString();
            bean.setStatus(Integer.parseInt(json.get(ILoggerBean.STATUS).toString()));
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.MESSAGE))) {
            bean.setMessage(json.get(ILoggerBean.MESSAGE).toString());
        } else {
            message = json.get(ILoggerBean.METHOD).toString() + " " + json.get(ILoggerBean.URL).toString() + " " + message;
            bean.setMessage(message);
        }

        if (json.get(ILoggerBean.ERROR_MESSAGE) != null) {
            bean.setErrorMessage(json.get(ILoggerBean.ERROR_MESSAGE).toString());
        }

        if (InputValidator.isNotNull(json.get(ILoggerBean.STACKTRACE))) {
            bean.setStacktrace(json.get(ILoggerBean.STACKTRACE).toString());
        }

        return bean;
    }
}
