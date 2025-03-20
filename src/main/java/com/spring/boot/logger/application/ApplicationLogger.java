package com.spring.boot.logger.application;

import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.config.LoggerConfig;
import com.spring.boot.logger.exceptions.ApiException;
import com.spring.boot.logger.utils.Dispatcher;
import com.spring.boot.logger.utils.ExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class ApplicationLogger extends AbstractApplicationLogger {

    public void info(String message) {
        JSONObject json = getLogJson(message);
        log.info(json.toJSONString());
    }

    public void error(String message) {
        JSONObject json = getLogJson(message);
        log.error(json.toJSONString());
    }

    public void info(String message, Object arg) {
        JSONObject json = getLogJson(message);
        log.info(json.toJSONString(), arg);
    }

    public void error(String message, Object arg) {
        JSONObject json = getLogJson(message);
        log.error(json.toJSONString(), arg);
    }

    public void error(ApiException e) {
        log.error(getLogJson(e).toJSONString());
    }

    private JSONObject getLogJson(String message) {
        JSONObject json = new JSONObject();

        json.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
        json.put(ILoggerBean.IS_SYSTEM_LOG, true);
        json.put(ILoggerBean.SERVICE, LoggerConfig.getService());
        json.put(ILoggerBean.MESSAGE, message);
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            ContentCachingRequestWrapper requestToCaching = new ContentCachingRequestWrapper(request);

            json.put(ILoggerBean.HEADERS, getHeaders(request).toString());
            json.put(ILoggerBean.METHOD, request.getMethod());
            json.put(ILoggerBean.URL, request.getRequestURI());
            json.put(ILoggerBean.REQUEST, maskingData(Dispatcher.getRequestData(requestToCaching)).toString());

            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            if (response != null) {
                ContentCachingResponseWrapper responseToCaching = new ContentCachingResponseWrapper(response);
                Map<String, Object> map = Dispatcher.responseMap(responseToCaching);
//                json.put(ILoggerBean.RESPONSE, map.get(Dispatcher.BODY));
                json.put(ILoggerBean.STATUS, map.get(Dispatcher.STATUS));
            }

        } catch (ParseException e) {
            json.put(ILoggerBean.ERROR_MESSAGE, e.getMessage());
            json.put(ILoggerBean.STACKTRACE, ExceptionUtils.getPrintStackTrace(e));
            json.put("reason", "[ApplicationLog] Log Body JSON PARSE ERROR");
        } catch (Exception e) {
            json.put(ILoggerBean.ERROR_MESSAGE, e.getMessage());
            json.put(ILoggerBean.STACKTRACE, ExceptionUtils.getPrintStackTrace(e));
            json.put("reason", "[ApplicationLog] Log ERROR");
        }

        return json;
    }

    private JSONObject getLogJson(ApiException apiException) {
        JSONObject json = new JSONObject();

        json.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
        json.put(ILoggerBean.IS_SYSTEM_LOG, true);
        json.put(ILoggerBean.SERVICE, LoggerConfig.getService());
//        json.put(ILoggerBean.MESSAGE, null);
        json.put(ILoggerBean.ERROR_MESSAGE, apiException.getMessage());
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            ContentCachingRequestWrapper requestToCaching = new ContentCachingRequestWrapper(request);

            json.put(ILoggerBean.HEADERS, getHeaders(request).toString());
            json.put(ILoggerBean.METHOD, request.getMethod());
            json.put(ILoggerBean.URL, request.getRequestURI());
            json.put(ILoggerBean.REQUEST, maskingData(Dispatcher.getRequestData(requestToCaching)).toString());

            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            if (response != null) {
                ContentCachingResponseWrapper responseToCaching = new ContentCachingResponseWrapper(response);
                Map<String, Object> map = Dispatcher.responseMap(responseToCaching);
//                json.put(ILoggerBean.RESPONSE, map.get(Dispatcher.BODY));
                json.put(ILoggerBean.STATUS, map.get(Dispatcher.STATUS));

                JSONObject res = new JSONObject();
                res.put("result", apiException.getCode());
                json.put(ILoggerBean.RESPONSE, res);
            }

        } catch (ParseException e) {
            json.put(ILoggerBean.ERROR_MESSAGE, e.getMessage());
            json.put(ILoggerBean.STACKTRACE, ExceptionUtils.getPrintStackTrace(e));
            json.put("reason", "[ApplicationLog] Log Body JSON PARSE ERROR");
        } catch (Exception e) {
            json.put(ILoggerBean.ERROR_MESSAGE, e.getMessage());
            json.put(ILoggerBean.STACKTRACE, ExceptionUtils.getPrintStackTrace(e));
            json.put("reason", "[ApplicationLog] Log ERROR");
        }

        return json;
    }
}

