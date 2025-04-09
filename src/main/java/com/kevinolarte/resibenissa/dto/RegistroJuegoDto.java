package com.kevinolarte.resibenissa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroJuegoDto {

    private Long idJuego;
    private Long idResidente;
    private Integer fallos;
    private Double duraccion;
}
