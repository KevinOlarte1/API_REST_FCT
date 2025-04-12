package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.Juego;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JuegoResponseDto {
    private String nombre;
    private Long idResidencia;

    public JuegoResponseDto(Juego juego){
        this.nombre = juego.getNombre();
        this.idResidencia = juego.getResidencia().getId();
    }
}
