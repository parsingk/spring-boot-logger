package com.spring.boot.logger.exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private HttpStatus statusCode;
    private int code;
    private String message;

    public ApiException(int code, String message) {
        super("Error Number " + code + " Error Message[]".replace("[", "[" + message));
        this.code = code;
        this.message = message;
    }

    public ApiException(int code, String message, Throwable e) {
        super("Error Number " + code + " Error Message[]".replace("[", "[" + message), e);
        this.code = code;
        this.message = message;
    }

    public ApiException(HttpStatus statusCode, int code, String message) {
        super("Error Number " + code + " Error Message[]".replace("[", "[" + message));
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    public ApiException(HttpStatus statusCode, int code, String message, Throwable e) {
        super("Error Number " + code + " Error Message[]".replace("[", "[" + message), e);
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    public int getCode() {
        return this.code;
    }
}
