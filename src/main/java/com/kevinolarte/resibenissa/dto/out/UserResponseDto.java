package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.User;
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

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.nombre = user.getNombre();
        this.apellido = user.getApellido();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.idResidencia = user.getResidencia().getId();
    }


}
