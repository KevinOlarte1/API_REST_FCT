package com.kevinolarte.resibenissa.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferencia de datos (DTO) utilizado para registrar una partida de juego
 * realizada por un residente en una residencia específica.
 * <p>
 * Incluye los datos mínimos necesarios para asociar el registro con el juego y la residencia,
 * así como la información del desempeño durante la sesión.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class RegistroJuegoDto {

    private Long idJuego;
    private Long idResidente;
    private Integer fallos;
    private Double duracion;
}
