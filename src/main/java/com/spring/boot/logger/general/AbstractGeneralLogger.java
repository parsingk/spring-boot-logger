package com.spring.boot.logger.general;

import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILogDTO;

public abstract class AbstractGeneralLogger extends AbstractLogger implements IGeneralLogger {

    protected boolean includeHeaders;

    public AbstractGeneralLogger() {
        this.includeHeaders = true;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public abstract void log(ILogDTO payloadObj);
}
