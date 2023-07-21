package com.spring.boot.logger.listeners;

import com.spring.boot.logger.application.SystemApplicationLogger;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    private final SystemApplicationLogger logger;

    public ApplicationStartedListener(SystemApplicationLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        logger.systemInfo("server started");
    }
}
