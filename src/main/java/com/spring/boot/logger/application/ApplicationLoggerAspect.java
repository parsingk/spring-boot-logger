package com.spring.boot.logger.application;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Aspect
@Order(0)
@Slf4j
public class ApplicationLoggerAspect {

    private final IApplicationLogger logger;

    public ApplicationLoggerAspect(IApplicationLogger logger) {
        this.logger = logger;
    }

    @Pointcut("within(@(@org.springframework.stereotype.Controller *) *) || @within(org.springframework.stereotype.Controller)")
    public void getControllerAnnotation() {
    }

    @Pointcut("getControllerAnnotation() && !@annotation(com.spring.boot.logger.annotations.NoApplicationLog) " +
            "&& !@annotation(com.spring.boot.logger.annotations.Error)")
    public void getApplicationLogAnnotation() {
    }

    @Pointcut("getControllerAnnotation() && !@annotation(com.spring.boot.logger.annotations.NoApplicationLog) " +
            "&& @annotation(com.spring.boot.logger.annotations.Error)")
    public void errorApplicationLogAnnotation() {
    }

    @Around(value = "getApplicationLogAnnotation()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return logger.doAround(joinPoint);
    }

    @Around(value = "errorApplicationLogAnnotation()")
    public Object onlyErrorAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return logger.errorAround(joinPoint);
    }
}
