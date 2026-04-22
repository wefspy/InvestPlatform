package com.example.investplatform.application.exception;

public class YookassaApiException extends RuntimeException {

    private final int statusCode;

    public YookassaApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public YookassaApiException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
