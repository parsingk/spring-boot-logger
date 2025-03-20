package com.spring.boot.logger.application;

public interface IApplicationLoggerFactory {

    void setLogger(AbstractApplicationLoggerAspect logger);

    AbstractApplicationLoggerAspect getLogger();
}
