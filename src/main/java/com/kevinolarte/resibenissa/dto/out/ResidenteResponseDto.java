package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.Residente;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

 @Getter
 @Setter
public class ResidenteResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Long idResidencia;

    public ResidenteResponseDto(Residente residente) {
        this.id = residente.getId();
        this.nombre = residente.getNombre();
        this.apellido = residente.getApellido();
        this.fechaNacimiento = residente.getFechaNacimiento();
        this.idResidencia = residente.getId();
    }
}
