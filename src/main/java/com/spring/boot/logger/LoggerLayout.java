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
import java.util.Map;

/**
 *  It's Custom Layout For Logback
 *
 *  Before you use LoggerLayout, you have to add dependencies - logback-json-classic, logback-jackson
 *  Then you can write in logback-spring.xml like this
 *
 *      <appender name="Custom" class="ch.qos.logback.core.ConsoleAppender">
 *         <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
 *             <layout class="com.spring.boot.logger.LoggerLayout">
 *                 <timestampFormat>yyyy-MM-dd'T'HH:mm:ss.SSSX</timestampFormat>
 *                 <timestampFormatTimezoneId>Etc/UTC</timestampFormatTimezoneId>
 *                 <jsonFormatter class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
 *                     <prettyPrint>true</prettyPrint>
 *                 </jsonFormatter>
 *             </layout>
 *         </encoder>
 *     </appender>
 */
public class LoggerLayout extends JsonLayout {

    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        map.remove(ILoggerBean.MESSAGE);

        JSONObject json = null;
        try {
            json = parseEventToJson(event);
        } catch (ParseException e) {
            map.put(ILoggerBean.MESSAGE, event.getMessage());
        }

        HashMap beanMap = null;

        try {
            if (json != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ILoggerBean bean = LoggerBeanAdapter.getBean(json);
                beanMap = objectMapper.convertValue(bean, HashMap.class);
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

        boolean isCustomLog = Boolean.parseBoolean(json.get(ILoggerBean.IS_CUSTOM_ERROR_LOG).toString());
        if (!isCustomLog) {
            Gson gson = new Gson();
            json = (JSONObject) parser.parse(gson.toJson(event.getMDCPropertyMap()));
        }

        return json;
    }
}
