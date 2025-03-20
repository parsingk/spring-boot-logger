package com.spring.boot.logger.general;

import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.ILogger;

import java.util.List;

public interface IGeneralLogger extends ILogger {

    void setIncludeHeaders(boolean includeHeaders);
    
    void log(ILogDTO payloadObj);

    void log(List<? extends ILogDTO> payloadList);

    void warn(ILogDTO payloadObj);

    void error(ILogDTO payloadObj);
}
