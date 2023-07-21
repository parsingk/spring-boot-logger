package com.spring.boot.logger.listeners;

import com.spring.boot.logger.application.SystemApplicationLogger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;

public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {


    private final SystemApplicationLogger logger;

    public ApplicationReadyListener(SystemApplicationLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.systemInfo("server ready. active : " + Arrays.toString(event.getApplicationContext().getEnvironment().getActiveProfiles()));
    }
}
