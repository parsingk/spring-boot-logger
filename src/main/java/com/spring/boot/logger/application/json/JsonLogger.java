package com.spring.boot.logger.application.json;

import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.AbstractApplicationLogger;
import com.spring.boot.logger.exceptions.ApiException;
import com.spring.boot.logger.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.simple.JSONObject;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Arrays;

@Slf4j
public class JsonLogger extends AbstractApplicationLogger {

    @Override
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        super.doAround(joinPoint);
        Object obj = null;
        HttpServletResponse response;

        JSONObject logJson = new JSONObject();
        logJson.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
        logJson.put(ILoggerBean.IS_CUSTOM_ERROR_LOG, false);
        long startTimeNanos = System.currentTimeMillis();
        try {
            // before processing
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            MDC.put(ILoggerBean.REQUEST, maskingData(parseRequestArgs(joinPoint.getArgs())).toString());
            MDC.put(ILoggerBean.HEADERS, getHeaders(request).toString());
            MDC.put(ILoggerBean.METHOD, request.getMethod());
            MDC.put(ILoggerBean.URL, request.getRequestURI());

            obj = joinPoint.proceed();

            // after processing
            response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            long endNanoTime = System.currentTimeMillis();
            if (response != null) {
                MDC.put(ILoggerBean.STATUS, String.valueOf(response.getStatus()));
            }

            MDC.put(ILoggerBean.EXECUTION_TIME, String.valueOf(endNanoTime - startTimeNanos));
            MDC.put(ILoggerBean.RESPONSE, parseJSON(obj).toString());

            log.info(logJson.toJSONString());
        } catch (Throwable e) {
            JSONObject errorResponseJson = new JSONObject();
            int result = ErrorCode.UNEXPECTED_ERROR;

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

            log.error(logJson.toJSONString());
            throw e;
        } finally {
            MDC.clear();
        }

        return obj;
    }
}
