package com.kevinolarte.resibenissa.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferencia de datos (DTO) utilizado para crear o actualizar un juego.
 * <p>
 * Este DTO permite asociar un nuevo juego a una residencia específica,
 * enviando únicamente el nombre del juego y el ID de la residencia.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class JuegoDto {
    private String nombre;
    private Long idResidencia;
}
