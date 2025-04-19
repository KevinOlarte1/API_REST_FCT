package com.kevinolarte.resibenissa.dto.in.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Objecto de transferencia de datos (DTO) utilizado para registrar por primera vez un usuaario.
 * <p>
 * Este DTO permite asociar un nuevo usuario a una residencia especifica,
 * enviando unicamente los siguentes parametros.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class RegisterUserDto {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Long idResidencia;
}
