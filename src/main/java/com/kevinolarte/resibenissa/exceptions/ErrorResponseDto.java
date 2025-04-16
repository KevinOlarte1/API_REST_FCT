package com.kevinolarte.resibenissa.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponseDto {

    private String mensaje;

    private int codigo;      // Código del enum

    private int status;      // Código HTTP

    private String timestamp;

    public ErrorResponseDto(String mensaje, int codigo, int status) {
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.status = status;
        this.timestamp = LocalDateTime.now().toString();
    }
}
