package com.spring.boot.logger.application;

import com.spring.boot.logger.utils.RequestContext;
import org.aspectj.lang.ProceedingJoinPoint;

public interface IApplicationLoggerAspect {

    Object doAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable;

    Object errorAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable;

    Object doExceptionHandlerAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable;
}
