package com.spring.boot.logger.application;

public interface IApplicationLoggerFactory {

    IApplicationLogger getLogger() throws Exception;
}
