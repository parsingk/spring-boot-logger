package com.spring.boot.logger;

public interface ILoggerBean {
    int APPLICATION_LOG = 0;
    int GENERAL_LOG = 1;

    String IS_EXCEPTION_OBJECT = "IS_EXCEPTION_OBJECT";
    String IS_CUSTOM_ERROR_LOG = "IS_CUSTOM_ERROR_LOG";

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

    String LEVEL_DEBUG = "DEBUG";
    String LEVEL_INFO = "INFO";
    String LEVEL_ERROR = "ERROR";

    String MESSAGE = "message";
    String ERROR_MESSAGE = "error_message";
    String MDC = "mdc";
    String JSONPAYLOAD = "jsonpayload";
    String STACKTRACE = "stacktrace";
}
