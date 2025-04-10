package com.kevinolarte.resibenissa.dto.in;

import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferencia de datos (DTO) utilizado para crear o actualizar una residencia.
 * <p>
 * Contiene los datos básicos necesarios para registrar una residencia en el sistema,
 * como su nombre y correo electrónico de contacto.
 * <p>
 * No contiene lógica de negocio ni persistencia.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class ResidenciaDto {

    private String nombre;
    private String email;
}
