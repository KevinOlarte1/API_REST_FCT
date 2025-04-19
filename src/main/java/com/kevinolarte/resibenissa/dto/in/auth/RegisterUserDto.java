package com.kevinolarte.resibenissa.dto.in.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Long idResidencia;
}
