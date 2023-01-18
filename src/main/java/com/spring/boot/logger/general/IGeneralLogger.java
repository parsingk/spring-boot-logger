package com.spring.boot.logger.general;

import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.ILogger;

public interface IGeneralLogger extends ILogger {

    void log(ILogDTO payloadObj);
}
