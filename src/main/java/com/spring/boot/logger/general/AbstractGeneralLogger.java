package com.spring.boot.logger.general;

import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILogDTO;

public abstract class AbstractGeneralLogger extends AbstractLogger implements IGeneralLogger {

    public abstract void log(ILogDTO payloadObj);
}
