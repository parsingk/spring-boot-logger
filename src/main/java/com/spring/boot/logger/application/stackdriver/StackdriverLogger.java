package com.spring.boot.logger.application.stackdriver;

import com.google.gson.Gson;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.AbstractApplicationLogger;
import com.spring.boot.logger.exceptions.ApiException;
import com.spring.boot.logger.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.simple.JSONObject;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class StackdriverLogger extends AbstractApplicationLogger {
    private final Gson gson = new Gson();
    private final int limitPayloadLength = 10 * 1024; // 10kb
    @Value("${request.logging.includeMethod:1}")
    private final boolean includeMethod = true;
    @Value("${request.logging.includeUri:1}")
    private final boolean includeUri = true;
    @Value("${request.logging.includeParameter:1}")
    private final boolean includeParameter = true;
    @Value("${request.logging.includeBodyRaw:1}")
    private final boolean includeBodyRaw = true;
    @Value("${request.logging.maxPayloadLength:1024}")
    private final int maxPayloadLength = 1024; // 1kb
    @Value("${request.logging.includeExecutionTime:1}")
    private final boolean includeExecutionTime = true;
    @Value("${request.logging.includeStatusCode:1}")
    private final boolean includeStatusCode = true;
    @Value("${request.logging.includeIpAddress:1}")
    private final boolean includeIpAddress = true;
    @Value("${request.logging.includeUserAgent:1}")
    private final boolean includeUserAgent = true;
    @Value("${request.logging.includeHeaders:1}")
    private final boolean includeHeaders = true;
    @Value("${request.logging.maskingKeys:password,passwordConfirm,newPassword,newPasswordConfirm}")
    private String[] maskingKeys = {"password","passwordConfirm","newPassword","newPasswordConfirm"};

    @Override
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        super.doAround(joinPoint);
        Object obj = null;

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String requestId = UUID.randomUUID().toString();
        MDC.put(StackdriverConstants.REQUEST_ID, requestId);

        JSONObject logJson = new JSONObject();
        logJson.put(ILoggerBean.LOG_TYPE, ILoggerBean.APPLICATION_LOG);
        logJson.put(ILoggerBean.IS_CUSTOM_ERROR_LOG, false);
        if (includeIpAddress) {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null)
                ip = request.getRemoteAddr();

            MDC.put("ip", ip);
        }

        if (includeMethod) {
            MDC.put(ILoggerBean.METHOD, request.getMethod());
        }

        if (includeUri) {
            MDC.put(ILoggerBean.URL, request.getRequestURI());
        }

        HttpServletRequest requestToUse = request;
        if (includeBodyRaw && !(request instanceof ContentCachingRequestWrapper)) {
            int length = Math.min(request.getContentLength(), limitPayloadLength);
            if (length > -1) {
                requestToUse = new ContentCachingRequestWrapper(request, length);
            }
        }

        JSONObject requestJson = parseRequestArgs(joinPoint.getArgs());
        MDC.put(ILoggerBean.REQUEST, maskingData(requestJson).toString());

        if (includeHeaders) {
            MDC.put(ILoggerBean.HEADERS, getHeaders(requestToUse).toString());
        }

        if (!requestJson.containsKey(ILoggerBean.IS_EXCEPTION_OBJECT)) {
            obj = this.doProcess(joinPoint, requestJson, logJson);
        } else {
            obj = this.doFromExceptionCall(joinPoint);
        }

        return obj;
    }


    private Object doProcess(ProceedingJoinPoint joinPoint, JSONObject requestJson, JSONObject logJson) throws Throwable {
        Object obj = null;
        HttpServletResponse response;
        long startTimeNanos = System.currentTimeMillis();

        try {
            obj = joinPoint.proceed();

            response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            long endNanoTime = System.currentTimeMillis();

            MDC.put(ILoggerBean.RESPONSE, parseJSON(obj).toString());

            if (includeStatusCode && response != null) {
                MDC.put(ILoggerBean.STATUS, String.valueOf(response.getStatus()));
            }

            if (includeExecutionTime) {
                MDC.put(ILoggerBean.EXECUTION_TIME, String.valueOf(endNanoTime - startTimeNanos));
            }

            log.info(logJson.toJSONString());
        }
        catch (Exception e) {
            String status = String.valueOf(ErrorCode.UNEXPECTED_ERROR);

            MDC.put(ILoggerBean.MESSAGE, e.getMessage());
            MDC.put(ILoggerBean.STACKTRACE, Arrays.toString(e.getStackTrace()));
            MDC.put(ILoggerBean.STATUS, status);

            if (e instanceof ApiException) {
                MDC.put(ILoggerBean.STATUS, String.valueOf(((ApiException) e).getCode()));
            }

            response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            long endNanoTime = System.currentTimeMillis();

            if (includeExecutionTime) {
                MDC.put(ILoggerBean.EXECUTION_TIME, String.valueOf(endNanoTime - startTimeNanos));
            }

            log.error(logJson.toJSONString());

            throw e;
        } finally {
            MDC.clear();
        }

        return obj;
    }

    private Object doFromExceptionCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } finally {
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            MDC.clear();
        }

        return obj;
    }
}
