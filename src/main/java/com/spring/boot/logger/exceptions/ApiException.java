package com.spring.boot.logger.exceptions;

public class ApiException extends RuntimeException {
    private int code;
    private String message;

    public ApiException(int code, String message) {
        super("Error Number " + code + " Error Message[]".replace("[", "[" + message));
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
