package com.spring.boot.logger;


import com.spring.boot.logger.utils.InputValidator;

public abstract class AbstractLogger implements ILogger {

    private static String service;

    public static String getService() {
        return service;
    }

    public static void setService(String service) {
        if (InputValidator.isBlankWithNull(AbstractLogger.service)) {
            AbstractLogger.service = service;
        }
    }
}
