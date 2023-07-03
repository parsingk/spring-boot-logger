package com.spring.boot.logger.application;

public interface IApplicationLoggerFactory {

    void setLogger(AbstractApplicationLogger logger);

    IApplicationLogger getLogger();
}
