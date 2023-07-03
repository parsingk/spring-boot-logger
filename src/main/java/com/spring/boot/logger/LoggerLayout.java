package com.spring.boot.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.spring.boot.logger.aws.AwsKinesisDataProducer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoggerLayout extends JsonLayout {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

        Map m = null;
        try {
            m = parseEventToJson(event);
        } catch (ParseException e) {
            map.put(ILoggerBean.MESSAGE, event.getMessage());
        }

        if (LoggerBeanAdapter.isSystemLog(m) && !hasHeaders(m)) {
            map.put(ILoggerBean.SERVICE, m.get(ILoggerBean.SERVICE));
            map.put(ILoggerBean.MESSAGE, m.get(ILoggerBean.MESSAGE));
            map.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
            m = null;
        }

        LinkedHashMap beanMap = null;
        try {
            if (m != null) {
                ILoggerBean bean = LoggerBeanAdapter.getBean(m);
                beanMap = objectMapper.convertValue(bean, LinkedHashMap.class);
            }
        } catch (Exception e) {
            map.put("Unexpected Error LoggerLayout", e.getMessage());
            map.put("Unexpected Error Message", e.getStackTrace());
            map.put(LEVEL_ATTR_NAME, ILoggerBean.LEVEL_ERROR);
        }

        if(m != null) {
            map.putAll(beanMap);
            map.remove(ILoggerBean.MDC);
        }

        if (AwsKinesisDataProducer.isConfigured()) {
            AwsKinesisDataProducer.getInstance().putRecord(map);
        }
    }

    private Map parseEventToJson(ILoggingEvent event) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(event.getMessage());

        if (LoggerBeanAdapter.isSystemLog(json)) return json;

        int logType = Integer.parseInt(json.getOrDefault(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG).toString());
        if (LoggerBeanAdapter.isGeneralLog(logType)) {
            return json;
        }

        Gson gson = new Gson();
        JSONObject mdc = (JSONObject) parser.parse(gson.toJson(event.getMDCPropertyMap()));
        json.putAll(mdc);

        return json;
    }

    private boolean hasHeaders(Map json) {
        if (json == null) return false;

        Object o = json.getOrDefault(ILoggerBean.HEADERS, null);
        if (o != null) {
            return true;
        }

        return false;
    }
}

