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
        String eventLoggerName = event.getLoggerName();

        if (isDefaultLogs(eventLoggerName)) {
            return;
        }

        if (isNeedParseJson(eventLoggerName)) {
            map.remove(ILoggerBean.MESSAGE);
            Map<String, Object> m = null;

            try {
                m = parseEventToJson(event);
            } catch (ParseException e) {
                add(FORMATTED_MESSAGE_ATTR_NAME, this.includeFormattedMessage, event.getFormattedMessage(), map);
            }

            try {
                if (m != null) {
                    ILoggerBean bean = LoggerBeanAdapter.getBean(m, eventLoggerName);
                    LinkedHashMap beanMap = objectMapper.convertValue(bean, LinkedHashMap.class);
                    map.putAll(beanMap);
                    map.remove(ILoggerBean.MDC);
                }
            } catch (Exception e) {
                map.put("Unexpected Error LoggerLayout", e.getMessage());
                map.put("Unexpected Error Message", e.getStackTrace());
                map.put(LEVEL_ATTR_NAME, ILoggerBean.LEVEL_ERROR);
            }
        }

        else if (LoggerNamesFactory.isSystemApplicationLogger(eventLoggerName)) {
            map.put(ILoggerBean.SERVICE, AbstractLogger.getService());
            map.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
        }

        if (AwsKinesisDataProducer.isConfigured()) {
            AwsKinesisDataProducer.getInstance().putRecord(map);
        }
    }

    private Map parseEventToJson(ILoggingEvent event) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(event.getMessage());

        if (LoggerNamesFactory.isApplicationLogger(event.getLoggerName()) || LoggerNamesFactory.isGeneralLogger(event.getLoggerName())) return json;

        Gson gson = new Gson();
        JSONObject mdc = (JSONObject) parser.parse(gson.toJson(event.getMDCPropertyMap()));
        json.putAll(mdc);

        return json;
    }

    private boolean isNeedParseJson(String eventLoggerName) {
        if (LoggerNamesFactory.isGeneralLogger(eventLoggerName) || LoggerNamesFactory.isJsonApplicationLogger(eventLoggerName) || LoggerNamesFactory.isApplicationLogger(eventLoggerName)) {
            return true;
        }

        return false;
    }

    private boolean isDefaultLogs(String eventLoggerName) {
        return !isNeedParseJson(eventLoggerName) && !LoggerNamesFactory.isSystemApplicationLogger(eventLoggerName);
    }
}

