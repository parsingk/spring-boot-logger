package com.spring.boot.logger.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.logger.AbstractLogger;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AbstractApplicationLogger extends AbstractLogger implements IApplicationLogger {

    protected static ObjectMapper objectMapper = ObjectMapperBuilder.build();

    protected JSONObject parseRequestArgs(Object[] args) {
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

    @Deprecated
    protected JSONObject parseRequestArgs(HttpServletRequest request) {
        JSONObject m = new JSONObject();
        try {
            Map params = request.getParameterMap();
            if (!params.isEmpty()) {
                params.forEach((key, value) -> {
                    try {
                        String[] values = (String[]) value;
                        if (key.equals("Packet")) {
                            m.putAll(new org.json.JSONObject(values[0]).toMap());
                        } else {
                            m.put(key, getParamValue(values));
                        }
                    } catch (Exception e) {
                    }
                });
            }

            ServletInputStream is = request.getInputStream();
            String body = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            if (!body.isEmpty()) {
                m.putAll(objectMapper.convertValue(body, org.json.JSONObject.class).toMap());
            }

            Object bodyData = request.getAttribute("$body_data");
            if (bodyData != null){
                m.putAll(objectMapper.convertValue(bodyData, HashMap.class));
            }

        } catch (Exception ignored) {
        }

        return m;
    }

    private String getParamValue(String[] values) {
        String v = "";
        for (String value : values) {
            v = v + value + ",";
        }

        return v.substring(0, v.length() - 1);
    }

    protected JSONObject maskingData(JSONObject data) {
        List<String> maskingKeys = ApplicationLoggerConfigurer.maskingKeys;

        if (maskingKeys == null) return data;

        JSONObject copyData = new JSONObject(data);
        for (String maskingKey : maskingKeys) {
            if (copyData.containsKey(maskingKey) && !Objects.isNull(copyData.get(maskingKey)) && Strings.isNotBlank(copyData.get(maskingKey).toString())) {
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

            String str;
            Object jsonObj = parser.parse(objectMapper.writeValueAsString(obj));
            str = jsonObj.toString();

            return str;
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

