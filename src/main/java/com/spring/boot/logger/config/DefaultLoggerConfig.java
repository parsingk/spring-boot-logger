package com.spring.boot.logger.config;

import com.spring.boot.logger.application.ApplicationLogger;
import com.spring.boot.logger.application.SystemApplicationLogger;
import com.spring.boot.logger.general.IGeneralLogger;
import com.spring.boot.logger.listeners.ApplicationFailedListener;
import com.spring.boot.logger.listeners.ApplicationReadyListener;
import com.spring.boot.logger.listeners.ApplicationStartedListener;
import com.spring.boot.logger.listeners.ApplicationStoppedListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DefaultLoggerConfig {

    private final DefaultListableBeanFactory factory;

    private final SystemApplicationLogger systemApplicationLogger = new SystemApplicationLogger();

    @Bean
    public ApplicationLogger applicationLogger() {
        factory.getBean(LoggerConfig.class);
        return new ApplicationLogger();
    }

    @Bean
    public IGeneralLogger generalLogger() throws Exception {
        return GeneralFactoryAdapter.getLogger();
    }

    @Bean
    public ApplicationStartedListener applicationStartedListener() {
        return new ApplicationStartedListener(systemApplicationLogger);
    }

    @Bean
    public ApplicationReadyListener applicationReadyListener() {
        return new ApplicationReadyListener(systemApplicationLogger);
    }

    @Bean
    public ApplicationFailedListener applicationFailedListener() {
        return new ApplicationFailedListener(systemApplicationLogger);
    }

    @Bean
    public ApplicationStoppedListener applicationStoppedListener() {
        return new ApplicationStoppedListener(systemApplicationLogger);
    }
}
