package com.kevinolarte.resibenissa.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResidenteDto {

    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Long residenciaId;
}
