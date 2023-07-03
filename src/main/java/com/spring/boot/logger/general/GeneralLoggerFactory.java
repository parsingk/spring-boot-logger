package com.spring.boot.logger.general;

import com.spring.boot.logger.config.IConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GeneralLoggerFactory implements IGeneralLoggerFactory {

//    final ConcurrentMap<String, Class<? extends IGeneralLogger>> loggerMap
//            = new ConcurrentHashMap<>();

    private final IGeneralLogger logger;

    public GeneralLoggerFactory() {
//        loggerMap.put(IConfiguration.GENERAL_LOGGING_TYPE_DEFAULT, GeneralLogger.class);
//        Class<? extends IGeneralLogger> logger = loggerMap.get(type);
//        if (logger == null) {
//            logger = loggerMap.get(IConfiguration.GENERAL_LOGGING_TYPE_DEFAULT);
//        }

        logger = new GeneralLogger();
    }

    @Override
    public void setIncludeHeaders(boolean includeHeaders) {
        logger.setIncludeHeaders(includeHeaders);
    }

    @Override
    public IGeneralLogger getLogger() {
        return logger;
    }
}
