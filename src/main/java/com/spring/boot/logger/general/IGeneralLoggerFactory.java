package com.spring.boot.logger.general;

public interface IGeneralLoggerFactory {

    void setIncludeHeaders(boolean includeHeaders);
    IGeneralLogger getLogger() throws Exception;
}
