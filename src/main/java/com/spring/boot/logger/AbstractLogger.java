package com.spring.boot.logger;


import com.spring.boot.logger.utils.InputValidator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class AbstractLogger implements ILogger {

    private static String service;

    private final DateFormat format;

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
            AbstractLogger.service = service;
        }
    }

    protected String formatTimestamp() {
        return format.format(new Date());
    }
}
