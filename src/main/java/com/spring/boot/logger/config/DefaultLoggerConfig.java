package com.spring.boot.logger.config;

import com.spring.boot.logger.application.ApplicationLogger;
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
        return new ApplicationStartedListener(applicationLogger());
    }

    @Bean
    public ApplicationReadyListener applicationReadyListener() {
        return new ApplicationReadyListener(applicationLogger());
    }

    @Bean
    public ApplicationFailedListener applicationFailedListener() {
        return new ApplicationFailedListener(applicationLogger());
    }

    @Bean
    public ApplicationStoppedListener applicationStoppedListener() {
        return new ApplicationStoppedListener(applicationLogger());
    }
}
