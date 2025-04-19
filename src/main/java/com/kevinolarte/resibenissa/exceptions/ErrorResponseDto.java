package com.kevinolarte.resibenissa.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de salida que representa la respuesta de error estándar en la API.
 * <p>
 * Esta clase encapsula toda la información relevante cuando ocurre una {@link ApiException}
 * y se desea devolver una respuesta estructurada al cliente.
 * </p>
 *
 * Contiene:
 * <ul>
 *     <li>Mensaje de error amigable.</li>
 *     <li>Código de error interno (definido en {@link ApiErrorCode}).</li>
 *     <li>Estado HTTP correspondiente.</li>
 *     <li>Fecha y hora del error.</li>
 * </ul>
 *
 * Este DTO es ideal para ser devuelto por un {@code @ControllerAdvice}.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class ErrorResponseDto {

    private String mensaje;

    private int codigo;      // Código del enum

    private int status;      // Código HTTP

    private String timestamp;

    public ErrorResponseDto(ApiException ex) {
        this.mensaje = ex.getErrorCode().getMessage();
        this.codigo = ex.getErrorCode().getCode();
        this.status = ex.getHttpStatus().value();
        this.timestamp = LocalDateTime.now().toString();
    }
}
