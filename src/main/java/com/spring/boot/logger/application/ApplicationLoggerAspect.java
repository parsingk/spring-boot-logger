package com.spring.boot.logger.application;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class ApplicationLoggerAspect {

    private final IApplicationLogger logger;

    public ApplicationLoggerAspect(IApplicationLogger logger) {
        this.logger = logger;
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && !@annotation(com.spring.boot.logger.annotations.NoApplicationLog)")
    public void getApplicationLogAnnotation() {}

    @Around(value = "getApplicationLogAnnotation()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return logger.doAround(joinPoint);
    }
}
