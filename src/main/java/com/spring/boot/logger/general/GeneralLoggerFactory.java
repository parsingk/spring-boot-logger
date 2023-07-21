package com.spring.boot.logger.general;


public class GeneralLoggerFactory implements IGeneralLoggerFactory {

    private final IGeneralLogger logger;

    public GeneralLoggerFactory() {
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
