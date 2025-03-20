package com.spring.boot.logger.general;

import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILogDTO;

import java.util.List;

public abstract class AbstractGeneralLogger extends AbstractLogger implements IGeneralLogger {

    protected boolean includeHeaders;

    public AbstractGeneralLogger() {
        this.includeHeaders = true;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public abstract void log(ILogDTO payloadObj);

    public abstract void log(List<? extends ILogDTO> payloadList);
}
