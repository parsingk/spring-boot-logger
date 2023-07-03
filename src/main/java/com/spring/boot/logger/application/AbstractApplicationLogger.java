package com.spring.boot.logger.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.utils.InputValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.MDC;

import java.util.*;

public abstract class AbstractApplicationLogger extends AbstractLogger implements IApplicationLogger {

    protected final ObjectMapper objectMapper = ObjectMapperBuilder.build();
    private static List<String> maskingKeys;

    private static int UNEXPECTED_ERROR_CODE = 500;


    public static void setMaskingKeys(List<String> maskingKeys) {
        if (InputValidator.isNull(AbstractApplicationLogger.maskingKeys)) {
            AbstractApplicationLogger.maskingKeys = maskingKeys;
        }
    }

    public static List<String> getMaskingKeys() {
        return maskingKeys;
    }

    public static void setUnexpectedErrorCode(int errorCode) {
        AbstractApplicationLogger.UNEXPECTED_ERROR_CODE = errorCode;
    }

    public static int getUnexpectedErrorCode() {
        return AbstractApplicationLogger.UNEXPECTED_ERROR_CODE;
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

    protected Map parseRequestArgs(Object[] args) {
        JSONObject m = new JSONObject();
        Iterator iterator;
        Object key;
        for (Object o : args) {
            try {
                HashMap map = objectMapper.convertValue(o, HashMap.class);
                iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();

                    m.put(key, map.get(key));
                }
            } catch (Exception ignored) {
            }
        }

        return m;
    }

    protected Map maskingData(Map data) {
        if (maskingKeys == null) return data;

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
            JSONObject json;
            if (byte[].class.equals(obj.getClass())) {
                json = (JSONObject) parser.parse(new String((byte[]) obj));
            } else {
                json = (JSONObject) parser.parse(obj.toString());
            }
            return json.toString();
        } catch (ParseException | JsonProcessingException e) {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, e);
        }
    }
}

