package com.spring.boot.logger.application;

import com.spring.boot.logger.ILogger;
import org.aspectj.lang.ProceedingJoinPoint;

public interface IApplicationLogger extends ILogger {

    Object doAround(ProceedingJoinPoint joinPoint) throws Throwable;

    Object errorAround(ProceedingJoinPoint joinPoint) throws Throwable;
}
