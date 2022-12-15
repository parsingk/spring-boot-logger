package com.spring.boot.logger.listeners;

import com.spring.boot.logger.application.ApplicationLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class ApplicationFailedListener implements ApplicationListener<ApplicationFailedEvent> {

    private final ApplicationLogger logger;

    public ApplicationFailedListener(ApplicationLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        log.error("server start failed. error : " + event.getException().getMessage());
    }
}
