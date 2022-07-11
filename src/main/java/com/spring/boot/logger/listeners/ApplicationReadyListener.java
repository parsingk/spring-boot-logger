package com.spring.boot.logger.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;

@Slf4j
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("server ready. active : " + Arrays.toString(event.getApplicationContext().getEnvironment().getActiveProfiles()));
    }
}
