package com.spring.boot.logger.application.json;

import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.AbstractApplicationLoggerAspect;
import com.spring.boot.logger.config.LoggerConfig;
import com.spring.boot.logger.exceptions.ApiException;
import com.spring.boot.logger.utils.ExceptionUtils;
import com.spring.boot.logger.utils.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Slf4j
public class JsonLogger extends AbstractApplicationLoggerAspect {

    @Override
    public Object doAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable {
        MDC.put(ILoggerBean.SERVICE, LoggerConfig.getService());
        MDC.put(ILoggerBean.LOG_TYPE, String.valueOf(ILoggerBean.APPLICATION_LOG));

        Object obj = null;

        long startTimeNanos = System.currentTimeMillis();
        try {
            beforeProcess(joinPoint);

            obj = joinPoint.proceed();

            afterProcess(obj, startTimeNanos);

            log.info("{}");
        } catch (ApiException e) {
            logForError(startTimeNanos, e);
            throw e;
        } catch (Exception ex) {
            requestContext.setBody(MDC.get(ILoggerBean.REQUEST));
            throw ex;
        } finally {
            MDC.clear();
        }

        return obj;
    }

    @Override
    public Object errorAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable {
        MDC.put(ILoggerBean.SERVICE, LoggerConfig.getService());
        MDC.put(ILoggerBean.LOG_TYPE, String.valueOf(ILoggerBean.APPLICATION_LOG));

        Object obj = null;

        long startTimeNanos = System.currentTimeMillis();
        try {
            beforeProcess(joinPoint);

            obj = joinPoint.proceed();
        } catch (ApiException e) {
            logForError(startTimeNanos, e);
            throw e;
        } catch (Exception ex) {
            requestContext.setBody(MDC.get(ILoggerBean.REQUEST));
            throw ex;
        } finally {
            MDC.clear();
        }

        return obj;
    }
    
    @Override
    public Object doExceptionHandlerAround(ProceedingJoinPoint joinPoint, RequestContext requestContext) throws Throwable {
        MDC.put(ILoggerBean.SERVICE, LoggerConfig.getService());
        MDC.put(ILoggerBean.LOG_TYPE, String.valueOf(ILoggerBean.APPLICATION_LOG));

        Object obj = null;

        long startTimeNanos = System.currentTimeMillis();
        try {
            beforeExceptionHandlerProcess(joinPoint, requestContext);

            obj = joinPoint.proceed();

            afterProcess(obj, startTimeNanos);

            log.error("{}");
        } finally {
            MDC.clear();
        }

        return obj;
    }

    private void beforeProcess(ProceedingJoinPoint joinPoint) {
        MDC.put(ILoggerBean.REQUEST_TIME, formatTimestamp());

        HttpServletRequest request = getRequest();

        MDC.put(ILoggerBean.REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(ILoggerBean.REQUEST, maskingData(parseRequestArgs(joinPoint.getArgs())).toString());
        MDC.put(ILoggerBean.METHOD, request.getMethod());
        MDC.put(ILoggerBean.URL, request.getRequestURI());
        MDC.put(ILoggerBean.HEADERS, getHeaders(request).toString());
    }

    private void afterProcess(Object obj, long startTimeNanos) throws ParseException {
        if (obj instanceof ResponseEntity<?> entity) {
            MDC.put(ILoggerBean.STATUS, String.valueOf(entity.getStatusCode().value()));
            MDC.put(ILoggerBean.RESPONSE, entity.hasBody() ? parseJSONString(entity.getBody()) : "");
        } else {
            MDC.put(ILoggerBean.STATUS, String.valueOf(HttpStatus.OK.value()));
            MDC.put(ILoggerBean.RESPONSE, parseJSONString(obj));
        }

        long endNanoTime = System.currentTimeMillis();
        MDC.put(ILoggerBean.EXECUTION_TIME, String.valueOf(endNanoTime - startTimeNanos));
    }

    private void beforeExceptionHandlerProcess(ProceedingJoinPoint joinPoint, RequestContext requestContext) {
        HttpServletRequest request = getRequest();

        MDC.put(ILoggerBean.REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(ILoggerBean.REQUEST, requestContext.getBody());
        MDC.put(ILoggerBean.METHOD, request.getMethod());
        MDC.put(ILoggerBean.URL, request.getRequestURI());
        MDC.put(ILoggerBean.HEADERS, getHeaders(request).toString());

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Throwable t) {
                MDC.put(ILoggerBean.ERROR_MESSAGE, t.getMessage());
                MDC.put(ILoggerBean.STACKTRACE, ExceptionUtils.getPrintStackTrace(t));
                if (t.getCause() != null) {
                    MDC.put(ILoggerBean.ERROR_DETAIL_MESSAGE, t.getCause().getMessage());
                }
            }
        }
    }

    private void logForError(long startTimeNanos, ApiException e) {
        HttpServletResponse response;
        JSONObject errorResponseJson = new JSONObject();
        JSONObject logJson = new JSONObject();
        int result = e.getCode();
        HttpStatus statusCode = e.getStatusCode();

        errorResponseJson.put("result", result);

        MDC.put(ILoggerBean.ERROR_MESSAGE, e.getMessage());
        MDC.put(ILoggerBean.STACKTRACE, ExceptionUtils.getPrintStackTrace(e));
        MDC.put(ILoggerBean.RESPONSE, errorResponseJson.toString());

        if (statusCode != null) {
            MDC.put(ILoggerBean.STATUS, String.valueOf(statusCode.value()));
        } else {
            response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            if (response != null) {
                MDC.put(ILoggerBean.STATUS, String.valueOf(response.getStatus()));
            }
        }

        long endNanoTime = System.currentTimeMillis();
        MDC.put(ILoggerBean.EXECUTION_TIME, String.valueOf(endNanoTime - startTimeNanos));

        logJson.put("exception", e.getClass().getName());
        log.error(logJson.toJSONString());
    }
}
