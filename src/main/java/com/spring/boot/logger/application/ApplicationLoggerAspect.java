package com.spring.boot.logger.application;

import com.spring.boot.logger.LoggerNamesFactory;
import com.spring.boot.logger.config.ApplicationFactoryAdapter;
import com.spring.boot.logger.utils.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(0)
@Slf4j
@Component
@ConditionalOnProperty(prefix = "logger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApplicationLoggerAspect {

    private final IApplicationLoggerAspect logger;
    private final RequestContext requestContext;

    public ApplicationLoggerAspect(@Autowired RequestContext requestContext) {
        this.logger = ApplicationFactoryAdapter.getLogger();
        this.requestContext = requestContext;
        LoggerNamesFactory.setApplicationJsonLoggerName(this.logger.getClass().getName());
    }

    @Pointcut("within(@(@org.springframework.stereotype.Controller *) *) || @within(org.springframework.stereotype.Controller)")
    public void getControllerAnnotation() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler) && !args(com.spring.boot.logger.exceptions.ApiException, *)")
    public void getExceptionAdviceAnnotation() {
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
        return logger.doAround(joinPoint, requestContext);
    }

    @Around(value = "errorApplicationLogAnnotation()")
    public Object onlyErrorAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return logger.errorAround(joinPoint, requestContext);
    }

    @Around(value = "getExceptionAdviceAnnotation()")
    public Object doExceptionAdviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return logger.doExceptionHandlerAround(joinPoint, requestContext);
    }
}
