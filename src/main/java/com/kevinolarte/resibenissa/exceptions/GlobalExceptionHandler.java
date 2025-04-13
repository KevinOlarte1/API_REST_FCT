package com.kevinolarte.resibenissa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getErrorCode().getMessage(),
                ex.getErrorCode().getCode(),
                ex.getHttpStatus().value()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }
}
