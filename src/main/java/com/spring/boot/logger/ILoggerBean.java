package com.spring.boot.logger;

import org.json.simple.parser.ParseException;

import java.util.Map;

public interface ILoggerBean {
    int APPLICATION_LOG = 0;
    int GENERAL_LOG = 1;

    String IS_SYSTEM_LOG = "IS_SYSTEM_LOG";

    String TIMESTAMP = "timestamp";
    String LEVEL = "level";
    String LOG_TYPE = "logtype";
    String SERVICE = "service";
    String HEADERS = "headers";
    String METHOD = "method";
    String URL = "url";
    String REQUEST = "request";
    String REQUEST_ID = "requestId";
    String RESPONSE = "response";
    String STATUS = "status";

    String BODY = "body";
    String PARAMETER = "parameter";
    String EXECUTION_TIME = "executiontime";
    String REQUEST_TIME = "requestTime";

    String LEVEL_DEBUG = "DEBUG";
    String LEVEL_INFO = "INFO";
    String LEVEL_ERROR = "ERROR";

    String MESSAGE = "message";
    String ERROR_MESSAGE = "error_message";
    String ERROR_DETAIL_MESSAGE = "error_detail_message";
    String MDC = "mdc";
    String JSONPAYLOAD = "jsonpayload";
    String STACKTRACE = "stacktrace";

    ILoggerBean create(Map json) throws ParseException;

    Integer getLogtype();
}
