package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.Residencia;

public class ResidenciaPublicResponseDto {
    public Long id;
    public String nombre;
    public String email;

    public ResidenciaPublicResponseDto(Residencia residencia) {
        this.id = residencia.getId();
        this.nombre = residencia.getNombre();
        this.email = residencia.getEmail();
    }
}
