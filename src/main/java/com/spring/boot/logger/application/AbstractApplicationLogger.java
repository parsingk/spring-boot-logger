package com.spring.boot.logger.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.utils.InputValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public abstract class AbstractApplicationLogger extends AbstractLogger implements IApplicationLogger {

    protected final ObjectMapper objectMapper = ObjectMapperBuilder.build();
    private static String applicationLoggingType;
    private static String[] maskingKeys;
    protected final int limitPayloadLength = 10 * 1024; // 10kb

    public static void setApplicationLoggingType(String applicationLoggingType) {
        if (InputValidator.isBlankWithNull(AbstractApplicationLogger.applicationLoggingType)) {
            AbstractApplicationLogger.applicationLoggingType = applicationLoggingType;
        }
    }

    public static String getApplicationLoggingType() {
        return applicationLoggingType;
    }

    public static void setMaskingKeys(String[] maskingKeys) {
        if (InputValidator.isNull(AbstractApplicationLogger.maskingKeys)) {
            AbstractApplicationLogger.maskingKeys = maskingKeys;
        }
    }

    public static String[] getMaskingKeys() {
        return maskingKeys;
    }

    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MDC.put(ILoggerBean.SERVICE, AbstractLogger.getService());
        MDC.put(ILoggerBean.LOG_TYPE, String.valueOf(ILoggerBean.APPLICATION_LOG));
        return MDC.getCopyOfContextMap();
    }

    public Object errorAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MDC.put(ILoggerBean.SERVICE, AbstractLogger.getService());
        MDC.put(ILoggerBean.LOG_TYPE, String.valueOf(ILoggerBean.APPLICATION_LOG));
        return MDC.getCopyOfContextMap();
    }

    protected JSONObject getHeaders(HttpServletRequest request) {
        JSONObject headers = new JSONObject();

        Enumeration<String> headerList = request.getHeaderNames();

        if(headerList != null) {
            String headerValue;
            while (headerList.hasMoreElements()) {
                headerValue = headerList.nextElement();
                headers.put(headerValue, request.getHeader(headerValue));
            }
        }

        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        headers.put("ip", ip);

        return headers;
    }

    protected JSONObject parseRequestArgs(Object[] args) {
        JSONObject json = new JSONObject();
        Iterator iterator;
        Object key;
        for (Object o : args) {
            try {
                HashMap map = objectMapper.convertValue(o, HashMap.class);
                iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();

                    json.put(key, map.get(key));
                }
            } catch (Exception ignored) {
            }
        }

        return json;
    }

    protected JSONObject maskingData(JSONObject data) {
        JSONObject copyData = new JSONObject(data);
        for (String maskingKey : maskingKeys) {
            if (copyData.containsKey(maskingKey)) {
                copyData.replace(maskingKey, "*******");
            }
        }
        return copyData;
    }

    protected String parseJSONString(Object obj) throws ParseException {
        JSONParser parser = new JSONParser();
        try {
            if (obj == null) {
                throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
            }

            JSONObject json = (JSONObject) parser.parse(objectMapper.writeValueAsString(obj));
            return json.toString();
        } catch (ClassCastException e) {
            if (obj == null) throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, e);
            JSONObject json = (JSONObject) parser.parse(obj.toString());
            return json.toString();
        } catch (ParseException | JsonProcessingException e) {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, e);
        }
    }
}

