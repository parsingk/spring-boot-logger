package com.spring.boot.logger.listeners;

import com.spring.boot.logger.application.SystemApplicationLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@Slf4j
public class ApplicationStoppedListener implements ApplicationListener<ContextClosedEvent> {

    private final SystemApplicationLogger logger;

    public ApplicationStoppedListener(SystemApplicationLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.systemInfo("server stopped");
    }
}
