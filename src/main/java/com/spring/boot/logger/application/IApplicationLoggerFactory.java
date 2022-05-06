package com.spring.boot.logger.application;

public interface IApplicationLoggerFactory {

    IApplicationLogger getLogger(String type, String service) throws Exception;
}
