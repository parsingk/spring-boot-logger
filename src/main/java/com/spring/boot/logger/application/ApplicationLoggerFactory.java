package com.spring.boot.logger.application;

import com.spring.boot.logger.application.json.JsonLogger;
import com.spring.boot.logger.config.IConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ApplicationLoggerFactory implements IApplicationLoggerFactory {

    AbstractApplicationLoggerAspect logger = null;

    public void setLogger(AbstractApplicationLoggerAspect logger) {
        if (this.logger == null) {
            this.logger = logger;
        }
    }

    @Override
    public AbstractApplicationLoggerAspect getLogger() {
        if (logger == null) {
            return new JsonLogger();
        }

        return logger;
    }
}
