package com.spring.boot.logger.general;

import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.annotations.GLog;

public class Logger {

    @GLog(level = "INFO")
    public ILogDTO info(ILogDTO obj) {
        return obj;
    }
}
