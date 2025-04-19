package com.kevinolarte.resibenissa.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {

    CAMPOS_OBLIGATORIOS(1001, "Faltan campos obligatorios", HttpStatus.BAD_REQUEST),
    CORREO_INVALIDO(1002, "Email invalid", HttpStatus.NOT_ACCEPTABLE),
    CORREO_DUPLICADO(1003, "Email ya existente", HttpStatus.CONFLICT),
    NOMBRE_DUPLICADO(1004, "Nombre ya existente", HttpStatus.CONFLICT),
    FECHA_INVALIDO(1005, "Fecha invalida", HttpStatus.NOT_ACCEPTABLE),
    RESIDENCIA_INVALIDO(1006, "Residencia invalida", HttpStatus.NOT_FOUND),
    VALORES_NEGATIVOS(1007,"No puede ser negativos los valores", HttpStatus.NOT_ACCEPTABLE),
    JUEGO_INVALIDO(1008, "Juego invalido", HttpStatus.NOT_FOUND),
    RESIDENTE_INVALIDO(1009, "Residente invalido", HttpStatus.NOT_FOUND),
    USUARIO_INVALIDO(1010, "Usuario invalido", HttpStatus.NOT_FOUND),
    CONFLICTO_REFERENCIAS(1011, "Problemas con las referencias de las entidades, no corresponden a las mismas", HttpStatus.CONFLICT),
    REGISTRO_JUEGO_INVALIDO(1012,"Registro juego invalido" , HttpStatus.NOT_FOUND ),
    REFERENCIAS_DEPENDIENTES(1013, "Esta entidad tiene referencias asociadas que dependen de el", HttpStatus.CONFLICT),
    USER_EXIST(1014, "Usuario ya existente" , HttpStatus.CONFLICT ),
    USER_NO_ACTIVADO(1015,"Usuario no activado" , HttpStatus.NOT_ACCEPTABLE ),
    CODIGO_EXPIRADO(1016,"El codigo de verificacion a caducado" , HttpStatus.GONE ),
    CODIGO_INVALIDO(1017,"El codigo de verificacion no es valdio" , HttpStatus.NOT_ACCEPTABLE ),
    USER_YA_ACTIVADO(1018,"El usuario ya activado" , HttpStatus.CONFLICT ),
    ERROR_MAIL_SENDER(1019,"Error enviando el correo" , HttpStatus.BAD_REQUEST ),
    ENDPOINT_PROTEGIDO(1020, "Tiene que tener permiso para acceder aqui", HttpStatus.METHOD_NOT_ALLOWED);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ApiErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
