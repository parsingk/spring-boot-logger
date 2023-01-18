package com.spring.boot.logger.listeners;

import com.spring.boot.logger.application.ApplicationLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    private final ApplicationLogger logger;

    public ApplicationStartedListener(ApplicationLogger logger) {
        this.logger = logger;
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info(logger.getSystemLogJson("server started").toJSONString());
    }
}
