package com.spring.boot.logger;


import com.spring.boot.logger.utils.InputValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

public abstract class AbstractLogger implements ILogger {

    private static String service;
    private final DateFormat format;
    protected final int limitPayloadLength = 10 * 1024; // 10kb

    public AbstractLogger() {
        this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone defaultTimeZone = TimeZone.getDefault();
        this.format.setTimeZone(defaultTimeZone);
    }

    public static String getService() {
        return service;
    }

    public static void setService(String service) {
        if (InputValidator.isBlankWithNull(AbstractLogger.service)) {
            AbstractLogger.service = service.toLowerCase();
        }
    }

    protected HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletRequest requestToUse = request;
        if (!(request instanceof ContentCachingRequestWrapper)) {
            int length = Math.min(request.getContentLength(), limitPayloadLength);
            if (length > -1) {
                requestToUse = new ContentCachingRequestWrapper(request, length);
            }
        }

        return requestToUse;
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

    protected String formatTimestamp() {
        return format.format(new Date());
    }
}
