package com.kevinolarte.resibenissa.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "Respuesta estándar de error para la API")
@Getter
@Setter
public class ErrorResponseDto {

    @Schema(description = "Mensaje de error", example = "Correo ya registrado")
    private String mensaje;

    @Schema(description = "Código de error definido por la aplicación", example = "1001")
    private int codigo;      // Código del enum

    @Schema(description = "Código de estado HTTP asociado", example = "409")
    private int status;      // Código HTTP

    @Schema(description = "Momento en que ocurrió el error", example = "2025-04-12T21:45:00")
    private String timestamp;

    public ErrorResponseDto(String mensaje, int codigo, int status) {
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.status = status;
        this.timestamp = LocalDateTime.now().toString();
    }
}
