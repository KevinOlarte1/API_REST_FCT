package com.kevinolarte.resibenissa.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferencia de datos (DTO) utilizado para crear o actualizar usuarios.
 * <p>
 * Contiene los campos necesarios para registrar un nuevo usuario en el sistema.
 * No debe contener lógica de negocio ni anotaciones de persistencia.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class UserDto {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Long residenciaId;
}
