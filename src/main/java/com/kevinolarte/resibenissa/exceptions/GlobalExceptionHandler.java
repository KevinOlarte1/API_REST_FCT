package com.kevinolarte.resibenissa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 * <p>
 * Captura y transforma excepciones de tipo {@link ApiException} en una respuesta estructurada
 * de tipo {@link ErrorResponseDto}, con el código, mensaje y estado HTTP correspondiente.
 * </p>
 *
 * Esta clase garantiza respuestas consistentes en todos los endpoints ante errores controlados.
 *
 * @author Kevin Olarte
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja cualquier excepción de tipo {@link ApiException} lanzada en los controladores.
     *
     * @param ex Excepción personalizada que contiene el código de error y estado HTTP.
     * @return {@link ResponseEntity} con el cuerpo {@link ErrorResponseDto} y el código HTTP correspondiente.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException ex) {
        ErrorResponseDto error =
                new ErrorResponseDto(ex);
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }
}
