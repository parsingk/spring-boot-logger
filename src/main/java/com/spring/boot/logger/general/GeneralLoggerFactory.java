package com.spring.boot.logger.general;

import com.spring.boot.logger.config.IConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GeneralLoggerFactory implements IGeneralLoggerFactory {

    final ConcurrentMap<String, Class<? extends IGeneralLogger>> loggerMap
            = new ConcurrentHashMap<>();

    public GeneralLoggerFactory() {
        loggerMap.put(IConfiguration.GENERAL_LOGGING_TYPE_DEFAULT, GeneralLogger.class);
    }

    @Override
    public IGeneralLogger getLogger(String type) throws Exception {
        Class<? extends IGeneralLogger> logger = loggerMap.get(type);
        if (logger == null) {
            logger = loggerMap.get(IConfiguration.GENERAL_LOGGING_TYPE_DEFAULT);
        }

        return logger.getDeclaredConstructor().newInstance();
    }
}
