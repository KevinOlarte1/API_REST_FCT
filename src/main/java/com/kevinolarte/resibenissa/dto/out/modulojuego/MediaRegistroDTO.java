package com.kevinolarte.resibenissa.dto.out.modulojuego;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaRegistroDTO {
    private String agrupacion;
    private Double promedio;
    private Long total;


    public MediaRegistroDTO(String agrupacion, Double promedio, Long total) {
        this.agrupacion = agrupacion;
        this.promedio = promedio;
        this.total = total;
    }
}
