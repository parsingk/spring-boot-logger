package com.spring.boot.logger.application;

import com.spring.boot.logger.application.json.JsonLogger;
import com.spring.boot.logger.config.IConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ApplicationLoggerFactory implements IApplicationLoggerFactory {

    AbstractApplicationLogger logger = null;

    public void setLogger(AbstractApplicationLogger logger) {
        if (this.logger == null) {
            this.logger = logger;
        }
    }

    @Override
    public IApplicationLogger getLogger() {
        if (logger == null) {
            return new JsonLogger();
        }

        return logger;
    }
}
