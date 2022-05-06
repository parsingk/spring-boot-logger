package com.spring.boot.logger.config;

import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.application.*;
import com.spring.boot.logger.general.GeneralLoggerFactory;
import com.spring.boot.logger.general.IGeneralLogger;
import com.spring.boot.logger.general.Logger;
import com.spring.boot.logger.general.LoggerAspect;
import com.spring.boot.logger.utils.InputValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.spring.boot.logger.config.IConfiguration.*;

@AutoConfigureAfter(value = { KinesisConfiguration.class })
@Configuration
public class LoggerConfiguration {

    @Value("${logging.application.type:#{null}}")
    private String applicationLoggingType;

    @Value("${service}")
    private String service;

    @Value("${logging.parameters.masking-keys:#{new String[0]}}")
    private String[] maskingKeys;

    @PostConstruct
    public void initService() throws Exception {
        // @Value annotation check this first. so doesn't need check for null here.
        if (InputValidator.isBlankWithNull(service)) {
            throw new Exception("service must not to be null in your application.yml or properties file.");
        }

        AbstractLogger.setService(service);
        AbstractApplicationLogger.setApplicationLoggingType(applicationLoggingType);
        AbstractApplicationLogger.setMaskingKeys(maskingKeys);
    }

    @Bean
    public Logger generalLogger() {
        return new Logger();
    }

    @Bean
    public LoggerAspect generalLoggerAspect() throws Exception {
        IGeneralLogger logger = new GeneralLoggerFactory().getLogger(IConfiguration.GENERAL_LOGGING_TYPE_DEFAULT);

        return new LoggerAspect(logger);
    }

    @Bean
    public ApplicationLogger applicationLogger() {
        return new ApplicationLogger();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = APPLICATION_ON, havingValue = VALUE_TRUE)
    public ApplicationLoggerAspect applicationLoggerAspect() throws Exception {
        IApplicationLogger logger = new ApplicationLoggerFactory().getLogger(applicationLoggingType, service);

        return new ApplicationLoggerAspect(logger);
    }
}
