package com.spring.boot.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.spring.boot.logger.aws.AwsKinesisDataProducer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoggerLayout extends JsonLayout {

    @Override
    protected Map toJsonMap(ILoggingEvent event) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        addTimestamp(TIMESTAMP_ATTR_NAME, this.includeTimestamp, event.getTimeStamp(), map);
        add(LEVEL_ATTR_NAME, this.includeLevel, String.valueOf(event.getLevel()), map);
        addMap(MDC_ATTR_NAME, this.includeMDC, event.getMDCPropertyMap(), map);
        add(FORMATTED_MESSAGE_ATTR_NAME, this.includeFormattedMessage, event.getFormattedMessage(), map);
        add(MESSAGE_ATTR_NAME, this.includeMessage, event.getMessage(), map);
        addCustomDataToJsonMap(map, event);

        add(THREAD_ATTR_NAME, this.includeThreadName, event.getThreadName(), map);
        add(LOGGER_ATTR_NAME, this.includeLoggerName, event.getLoggerName(), map);
        add(CONTEXT_ATTR_NAME, this.includeContextName, event.getLoggerContextVO().getName(), map);
        addThrowableInfo(EXCEPTION_ATTR_NAME, this.includeException, event, map);

        return map;
    }

    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        map.remove(ILoggerBean.MESSAGE);

        JSONObject json = null;
        try {
            json = parseEventToJson(event);
        } catch (ParseException e) {
            map.put(ILoggerBean.MESSAGE, event.getMessage());
        }

        if (isSystemLog(json)) {
            map.put(ILoggerBean.SERVICE, json.get(ILoggerBean.SERVICE));
            map.put(ILoggerBean.MESSAGE, json.get(ILoggerBean.MESSAGE));
            map.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
            json = null;
        }

        LinkedHashMap beanMap = null;
        try {
            if (json != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ILoggerBean bean = LoggerBeanAdapter.getBean(json);
                beanMap = objectMapper.convertValue(bean, LinkedHashMap.class);
            }
        } catch (Exception e) {
            map.put("Unexpected Error LoggerLayout", e.getMessage());
            map.put("Unexpected Error Message", e.getStackTrace());
        }

        if(beanMap != null) {
            map.putAll(beanMap);
            map.remove(ILoggerBean.MDC);
        }

        if (AwsKinesisDataProducer.isConfigured()) {
            AwsKinesisDataProducer.getInstance().putRecord(AbstractLogger.getService(), map);
        }
    }

    private JSONObject parseEventToJson(ILoggingEvent event) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(event.getMessage());

        if (isSystemLog(json)) return json;

        boolean isCustomLog = Boolean.parseBoolean(json.get(ILoggerBean.IS_CUSTOM_ERROR_LOG).toString());
        if (!isCustomLog) {
            Gson gson = new Gson();
            json = (JSONObject) parser.parse(gson.toJson(event.getMDCPropertyMap()));
        }

        return json;
    }

    private boolean isSystemLog(JSONObject json) {
        if (json == null) return false;
        return Boolean.parseBoolean(json.getOrDefault(ILoggerBean.IS_SYSTEM_LOG, false).toString());
    }
}

