package com.spring.boot.logger.general;

import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.annotations.GLog;
import org.aspectj.lang.JoinPoint;

public abstract class AbstractGeneralLogger extends AbstractLogger implements IGeneralLogger {

    public abstract void afterReturning(JoinPoint joinPoint, GLog gLog, ILogDTO payloadObj);
}
