package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResidenciaResponseDto {
    private Long id;
    private String nombre;
    private String email;
    private List<Long> usuarios;
    private List<Long> residentes;

    public ResidenciaResponseDto(Residencia residencia) {
        this.id = residencia.getId();
        this.nombre = residencia.getNombre();
        this.email = residencia.getEmail();

        this.usuarios = residencia.getUsuarios()
                .stream()
                .map(User::getId)
                .toList();

        this.residentes = residencia.getResidentes()
                .stream()
                .map(Residente::getId)
                .toList();
    }

}


