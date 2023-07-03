package com.spring.boot.logger.config;

import com.spring.boot.logger.general.GeneralLoggerFactory;
import com.spring.boot.logger.general.IGeneralLogger;
import com.spring.boot.logger.general.IGeneralLoggerFactory;

public class GeneralFactoryAdapter {

    private static final IGeneralLoggerFactory loggerFactory = new GeneralLoggerFactory();


    static void setIncludeHeaders(boolean includeHeaders) {
        loggerFactory.setIncludeHeaders(includeHeaders);
    }

    public static IGeneralLogger getLogger() throws Exception {
        return loggerFactory.getLogger();
    }
}
