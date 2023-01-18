package com.spring.boot.logger.application;

import com.spring.boot.logger.application.json.JsonLogger;
import com.spring.boot.logger.config.IConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ApplicationLoggerFactory implements IApplicationLoggerFactory {

    final ConcurrentMap<String, Class<? extends IApplicationLogger>> loggerMap
            = new ConcurrentHashMap<>();


    public ApplicationLoggerFactory() {
        loggerMap.put(IConfiguration.APPLICATION_LOGGING_TYPE_JSON, JsonLogger.class);
//        loggerMap.put(IConfiguration.APPLICATION_LOGGING_TYPE_STACKDRIVER, StackdriverLogger.class);
    }

    @Override
    public IApplicationLogger getLogger() throws Exception {
        if (!loggerMap.containsKey(AbstractApplicationLogger.getApplicationLoggingType())) {
            throw new Exception("Can not find logger by logging.type in your application.yml or properties file.");
        }

        Class<? extends IApplicationLogger> logger = loggerMap.get(AbstractApplicationLogger.getApplicationLoggingType());

        return logger.getDeclaredConstructor().newInstance();
    }
}
