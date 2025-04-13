package com.kevinolarte.resibenissa.exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException{

    private ApiErrorCode errorCode;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return errorCode.getHttpStatus().value();
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
