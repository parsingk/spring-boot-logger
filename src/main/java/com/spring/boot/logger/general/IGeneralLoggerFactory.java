package com.spring.boot.logger.general;

public interface IGeneralLoggerFactory {

    IGeneralLogger getLogger(String type) throws Exception;
}
