package com.kevinolarte.resibenissa.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private boolean enabled;
    private Long idResidencia;

    public UserResponseDto(Long id, String nombre, String apellido, String email, boolean enabled, Long idResidencia) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.enabled = enabled;
        this.idResidencia = idResidencia;
    }


}
