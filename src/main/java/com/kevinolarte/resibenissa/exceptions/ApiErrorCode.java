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
    RESIDENCIA_INVALIDO(1006, "Residencia invalida", HttpStatus.BAD_REQUEST),
    VALORES_NEGATIVOS(1007,"No puede ser negativos los valores", HttpStatus.BAD_REQUEST),
    JUEGO_INVALIDO(1008, "Juego invalido", HttpStatus.BAD_REQUEST),
    RESIDENTE_INVALIDO(1009, "Residente invalido", HttpStatus.BAD_REQUEST),
    USUARIO_INVALIDO(1010, "Usuario invalido", HttpStatus.BAD_REQUEST),
    CONFLICTO_REFERENCIAS(1011, "Problemas con las referencias de las entidades, no corresponden a las mismas", HttpStatus.CONFLICT),
    REGISTRO_JUEGO_INVALIDO(1012,"Registro juego invalido" , HttpStatus.BAD_REQUEST );

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ApiErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
