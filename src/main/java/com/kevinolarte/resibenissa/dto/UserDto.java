package com.kevinolarte.resibenissa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Long residenciaId;
}
