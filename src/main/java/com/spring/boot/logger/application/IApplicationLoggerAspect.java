package com.spring.boot.logger.application;

import com.spring.boot.logger.utils.RequestContext;
import org.aspectj.lang.ProceedingJoinPoint;

public interface IApplicationLoggerAspect {

    /**
     * AOP Around - Method with @Controller Annotation.
     * Except Method with @NoApplicationLog and @Error Annotation.
     *
     * @param joinPoint
     * @param requestContext
     * @return
     * @throws Throwable
     */
    Object doAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable;

    /**
     * AOP Around - Method with @Controller and @Error Annotation.
     * Except Method with @NoApplicationLog Annotation.
     *
     * @param joinPoint
     * @param requestContext
     * @return
     * @throws Throwable
     */
    Object errorAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable;

    /**
     * AOP Around - Method with @ExceptionHandler Annotation.
     * Except ApiException class.
     *
     * @param joinPoint
     * @param requestContext
     * @return
     * @throws Throwable
     */
    Object doExceptionHandlerAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable;
}
