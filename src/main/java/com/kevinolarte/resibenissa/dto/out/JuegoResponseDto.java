package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.Juego;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JuegoResponseDto {
    private Long id;
    private String nombre;
    private Long idResidencia;

    public JuegoResponseDto(Juego juego){
        this.id = juego.getId();
        this.nombre = juego.getNombre();
        this.idResidencia = juego.getResidencia().getId();
    }
}
