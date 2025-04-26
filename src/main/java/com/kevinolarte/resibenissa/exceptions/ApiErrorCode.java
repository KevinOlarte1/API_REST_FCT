package com.kevinolarte.resibenissa.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enumeración que define los distintos códigos de error personalizados utilizados en la API.
 * <p>
 * Cada valor del enum representa un tipo específico de error que puede ocurrir en la lógica
 * de negocio de la aplicación. Cada error contiene:
 * <ul>
 *   <li>Un código numérico interno único para facilitar el rastreo.</li>
 *   <li>Un mensaje amigable que puede mostrarse al usuario.</li>
 *   <li>Un {@link HttpStatus} que será devuelto como código HTTP en la respuesta.</li>
 * </ul>
 *
 * <p>Esta clase se utiliza junto a {@link ApiException} para lanzar errores consistentes.</p>
 *
 * @see ApiException
 * @author Kevin Olarte
 */
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
    ENDPOINT_PROTEGIDO(1020, "Tiene que tener permiso para acceder aqui", HttpStatus.METHOD_NOT_ALLOWED),
    PROBLEMAS_CON_FILE(1021,"A surgido un problema con la imagen" , HttpStatus.INTERNAL_SERVER_ERROR ),
    PARTICIPANTE_YA_REGISTRADO(1022,"Este residente ya participa en otro evento ese mismo dia" ,HttpStatus.NOT_ACCEPTABLE),
    EVENTO_SALIDA_INVALIDO(1023,"EventoSalida invalida" ,HttpStatus.NOT_FOUND),
    EVENTO_SALIDA_NO_DISPONIBLE(1024, "No se puede hacer nada con este evento ciertos problemas", HttpStatus.BAD_REQUEST),
    PARTICIPANTE_INVALIDO(1025, "Participante invalido" , HttpStatus.NOT_FOUND),
    PARTICIPANTE_INMUTABLE(1026, "No se puede hacer esta operacion con el participante porque ahora mismo es inmutable", HttpStatus.NOT_ACCEPTABLE);


    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ApiErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
