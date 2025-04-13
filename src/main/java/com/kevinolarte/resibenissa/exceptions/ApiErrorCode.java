package com.kevinolarte.resibenissa.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {

    CAMPOS_OBLIGATORIOS(1001, "Faltan campos obligatorios", HttpStatus.BAD_REQUEST),
    CORREO_INVALIDO(1002, "Email invalid", HttpStatus.BAD_REQUEST),
    CORREO_DUPLICADO(1003, "Email ya existente", HttpStatus.CONFLICT),
    NOMBRE_DUPLICADO(1004, "Nombre ya existente", HttpStatus.CONFLICT),
    FECHA_INVALIDO(1005, "Fecha invalida", HttpStatus.BAD_REQUEST),
    RESIDENCIA_INVALIDO(1006, "Residencia invalida", HttpStatus.BAD_REQUEST),;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ApiErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
