package com.spring.boot.logger.general;

import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.annotations.GLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class LoggerAspect {

    private final IGeneralLogger logger;

    public LoggerAspect(IGeneralLogger logger) {
        this.logger = logger;
    }

    @Pointcut("@annotation(com.spring.boot.logger.annotations.GLog) && @annotation(gLog)")
    public void getGeneralLogAnnotation(GLog gLog) {}

    @AfterReturning(value = "getGeneralLogAnnotation(gLog)", returning = "payloadObj", argNames = "joinPoint,gLog,payloadObj")
    public void afterReturning(JoinPoint joinPoint, GLog gLog, ILogDTO payloadObj) {
        logger.afterReturning(joinPoint, gLog, payloadObj);
    }
}
