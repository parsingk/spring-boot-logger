package com.spring.boot.logger.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.logger.utils.InputValidator;

import java.util.List;

public class ApplicationLoggerConfigurer implements IApplicationLoggerConfigurer {

    protected static List<String> maskingKeys;

    @Override
    public void setMaskingKeys(List<String> maskingKeys) {
        if (InputValidator.isNull(ApplicationLoggerConfigurer.maskingKeys)) {
            ApplicationLoggerConfigurer.maskingKeys = maskingKeys;
        }
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        AbstractApplicationLogger.objectMapper = objectMapper.copy();
    }

}
