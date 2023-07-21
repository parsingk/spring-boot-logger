package com.spring.boot.logger.application.json;

import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.AbstractApplicationLogger;
import com.spring.boot.logger.aws.AwsKinesisDataProducer;
import com.spring.boot.logger.exceptions.ApiException;
import com.spring.boot.logger.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JsonLogger extends AbstractApplicationLogger {

    @Override
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        super.doAround(joinPoint);
        Object obj = null;

        JSONObject logJson = new JSONObject();
//        logJson.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
        long startTimeNanos = System.currentTimeMillis();
        try {
            beforeProcess(joinPoint);

            obj = joinPoint.proceed();

            afterProcess(obj, startTimeNanos);

            log.info(logJson.toJSONString());
        } catch (Throwable e) {
            logForError(logJson, startTimeNanos, e);
            throw e;
        } finally {
            MDC.clear();
        }

        return obj;
    }

    @Override
    public Object errorAround(ProceedingJoinPoint joinPoint) throws Throwable {
        super.doAround(joinPoint);
        Object obj = null;

        JSONObject logJson = new JSONObject();
//        logJson.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
        long startTimeNanos = System.currentTimeMillis();
        try {
            beforeProcess(joinPoint);

            obj = joinPoint.proceed();
        } catch (Throwable e) {
            logForError(logJson, startTimeNanos, e);
            throw e;
        } finally {
            MDC.clear();
        }

        return obj;
    }

    private void beforeProcess(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = getRequest();

        MDC.put(ILoggerBean.REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(ILoggerBean.REQUEST, maskingData(parseRequestArgs(joinPoint.getArgs())).toString());
        MDC.put(ILoggerBean.METHOD, request.getMethod());
        MDC.put(ILoggerBean.URL, request.getRequestURI());
        MDC.put(ILoggerBean.HEADERS, getHeaders(request).toString());
    }

    private void afterProcess(Object obj, long startTimeNanos) throws ParseException {
        HttpServletResponse response;
        response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        long endNanoTime = System.currentTimeMillis();
        if (response != null) {
            MDC.put(ILoggerBean.STATUS, String.valueOf(response.getStatus()));
        }
        MDC.put(ILoggerBean.EXECUTION_TIME, String.valueOf(endNanoTime - startTimeNanos));
        MDC.put(ILoggerBean.RESPONSE, parseJSONString(obj));
    }

    private void logForError(JSONObject logJson, long startTimeNanos, Throwable e) {
        HttpServletResponse response;
        JSONObject errorResponseJson = new JSONObject();
        int result = AbstractApplicationLogger.getUnexpectedErrorCode();

        if(e instanceof ApiException) {
            result = ((ApiException) e).getCode();
        }

        if(e instanceof SQLException) {
            result = ((SQLException) e).getErrorCode();
        }

        errorResponseJson.put("result", result);

        MDC.put(ILoggerBean.MESSAGE, e.getMessage());
        MDC.put(ILoggerBean.STACKTRACE, Arrays.toString(e.getStackTrace()));
        MDC.put(ILoggerBean.RESPONSE, errorResponseJson.toString());

        response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        if (response != null) {
            MDC.put(ILoggerBean.STATUS, String.valueOf(response.getStatus()));
        }

        long endNanoTime = System.currentTimeMillis();
        MDC.put(ILoggerBean.EXECUTION_TIME, String.valueOf(endNanoTime - startTimeNanos));

        logJson.put("exception", e.getClass().getName());
        log.error(logJson.toJSONString());
    }
}
