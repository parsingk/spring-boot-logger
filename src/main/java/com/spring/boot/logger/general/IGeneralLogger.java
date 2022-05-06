package com.spring.boot.logger.general;

import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.ILogger;
import com.spring.boot.logger.annotations.GLog;
import org.aspectj.lang.JoinPoint;

public interface IGeneralLogger extends ILogger {

    void afterReturning(JoinPoint joinPoint, GLog gLog, ILogDTO payloadObj);
}
