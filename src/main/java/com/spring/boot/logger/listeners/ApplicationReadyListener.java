package com.spring.boot.logger.listeners;

import com.spring.boot.logger.application.ApplicationLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;

@Slf4j
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {


    private final ApplicationLogger logger;

    public ApplicationReadyListener(ApplicationLogger logger) {
        this.logger = logger;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info(logger.getSystemLogJson("server ready. active : " + Arrays.toString(event.getApplicationContext().getEnvironment().getActiveProfiles())).toJSONString());
    }
}
