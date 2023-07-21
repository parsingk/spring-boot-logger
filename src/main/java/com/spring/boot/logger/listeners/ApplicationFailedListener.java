package com.spring.boot.logger.listeners;

import com.spring.boot.logger.application.SystemApplicationLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class ApplicationFailedListener implements ApplicationListener<ApplicationFailedEvent> {

    private final SystemApplicationLogger logger;

    public ApplicationFailedListener(SystemApplicationLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        logger.systemError("server start failed. error : " + event.getException().getMessage());
    }
}
