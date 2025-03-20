package com.spring.boot.logger.application;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public interface IApplicationLoggerConfigurer {

    void setMaskingKeys(List<String> maskingKeys);

    void setObjectMapper(ObjectMapper objectMapper);
}
