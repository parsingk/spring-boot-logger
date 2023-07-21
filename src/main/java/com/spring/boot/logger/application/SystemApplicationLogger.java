package com.spring.boot.logger.application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemApplicationLogger {

    public void systemInfo(String message) {
        log.info(message);
    }

    public void systemWarn(String message) {
        log.warn(message);
    }

    public void systemError(String message) {
        log.error(message);
    }
}
