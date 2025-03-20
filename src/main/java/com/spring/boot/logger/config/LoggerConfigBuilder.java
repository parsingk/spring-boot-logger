package com.spring.boot.logger.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.logger.application.AbstractApplicationLogger;
import com.spring.boot.logger.application.AbstractApplicationLoggerAspect;
import com.spring.boot.logger.application.AbstractApplicationLoggerBean;
import com.spring.boot.logger.application.IApplicationLogger;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class LoggerConfigBuilder {

    private final String service;
    private List<String> maskingKeys;
    private ObjectMapper objectMapper;

    public LoggerConfigBuilder(@NotBlank String service) {
        this.service = service;
    }

    public LoggerConfigBuilder maskingKeys(@NotNull List<String> maskingKeys) {
        this.maskingKeys = maskingKeys;
        return this;
    }

    public LoggerConfigBuilder objectMapper(@NotNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public LoggerConfigBuilder applicationLogger(@NotNull AbstractApplicationLoggerAspect applicationLogger) {
        ApplicationFactoryAdapter.setLogger(applicationLogger);
        return this;
    }

    public LoggerConfigBuilder applicationLoggerBean(@NotNull Class<? extends AbstractApplicationLoggerBean> bean) {
        ApplicationFactoryAdapter.setBean(bean);
        return this;
    }

    public LoggerConfigBuilder generalLoggerIncludeHeaders(boolean includeHeaders) {
        GeneralFactoryAdapter.setIncludeHeaders(includeHeaders);
        return this;
    }

    public LoggerConfig build() {
        ApplicationFactoryAdapter.loggerInitializer.setMaskingKeys(maskingKeys);

        if (Objects.nonNull(objectMapper)) {
            ApplicationFactoryAdapter.loggerInitializer.setObjectMapper(objectMapper);
        }

        return new LoggerConfig().setService(service);
    }
}
