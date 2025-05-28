package com.kevinolarte.resibenissa.exceptions;

import com.kevinolarte.resibenissa.services.LoggerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para la API REST.
 * <p>
 * Captura y transforma excepciones de tipo {@link ResiException} en una respuesta estructurada
 * de tipo {@link ErrorResponseDto}, con el código, mensaje y estado HTTP correspondiente.
 * </p>
 *
 * Esta clase garantiza respuestas consistentes en todos los endpoints ante errores controlados.
 *
 * @author Kevin Olarte
 */
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private LoggerService loggerService;
    /**
     * Maneja cualquier excepción de tipo {@link ResiException} lanzada en los controladores.
     *
     * @param ex Excepción personalizada que contiene el código de error y estado HTTP.
     * @return {@link ResponseEntity} con el cuerpo {@link ErrorResponseDto} y el código HTTP correspondiente.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException ex) {
        loggerService.registrarLogError(ex.getMensaje());
        ErrorResponseDto error =
                new ErrorResponseDto(ex.getResiException());
        return ResponseEntity.status(ex.getResiException().getHttpStatus()).body(error);
    }


}
