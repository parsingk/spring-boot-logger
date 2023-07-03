package com.spring.boot.logger.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {

    private final static ObjectMapper objectMapper = new ObjectMapper().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    private final static JSONParser parser = new JSONParser();

    public final static String HEADER = "header";
    public final static String STATUS = "status";
    public final static String BODY = "body";

    public static JSONObject getParameters(ContentCachingRequestWrapper request) throws IOException {
        JSONObject json = new JSONObject();
        Enumeration<String> paramNames = request.getParameterNames();

        String paramName;
        String val;
        Object parsed;
        while (paramNames.hasMoreElements()) {
            paramName = paramNames.nextElement();
            val = request.getParameter(paramName);
            try {
                parsed = parser.parse(val);
                json.put(paramName, parsed);
            } catch (ParseException e) {
                json.put(paramName, val);
            }
        }

        return json;
    }

    public static String getBody(ContentCachingRequestWrapper request) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String buffer;
        while ((buffer = input.readLine()) != null) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(buffer);
        }
        return builder.toString();
    }

    public static JSONObject getRequestData(ContentCachingRequestWrapper request) throws IOException, ParseException {
        JSONObject params = getParameters(request);
        String body = getBody(request);
        JSONObject bodyJson = new JSONObject();

        if (!body.isBlank()) {
            try {
                bodyJson = (JSONObject) parser.parse(body);
            } catch (ParseException e) {
                String[] bodyParams = body.split("&");
                String[] splitted;
                for (String element : bodyParams) {
                    splitted = element.split("=");
                    if (splitted.length > 1) {
                        bodyJson.put(splitted[0], splitted[1]);
                    }
                }
            }

        }

        return merge(params, bodyJson);
    }

    private static JSONObject merge(JSONObject params, JSONObject body) {
        if (params.isEmpty()) {
            return body;
        }

        if (body.isEmpty()) {
            return params;
        }

        HashMap map = objectMapper.convertValue(body, HashMap.class);

        params.putAll(map);

        return params;
    }

    public static Map<String, Object> responseMap(ContentCachingResponseWrapper response) throws IOException {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(STATUS, response.getStatus());

        Map<String, Object> responseHeaderMap = new HashMap<>();
        Collection<String> responseHeaderNameList = response.getHeaderNames();
        responseHeaderNameList.forEach(v -> responseHeaderMap.put(v, response.getHeader(v)));
        responseMap.put(HEADER, responseHeaderMap);

        byte[] bytes = response.getContentAsByteArray();
        Object responseBody = objectMapper.readValue(bytes, Object.class);
        responseMap.put(BODY, responseBody);

        return responseMap;
    }
}
