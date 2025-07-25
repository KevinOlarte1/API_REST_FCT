package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.Residente;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de salida que representa los datos públicos de un residente.
 * <p>
 * Esta clase se utiliza para enviar al cliente información relevante sobre
 * un residente sin exponer detalles internos del modelo ni relaciones sensibles.
 * </p>
 *
 * Contiene campos como el ID, nombre completo, fecha de nacimiento y la residencia asociada.
 *
 * @author Kevin Olate
 */
 @Getter
 @Setter
public class ResidenteResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String documentoIdentidad;
    private String familiar1;
    private String familiar2;
    private Long idResidencia;
    private Boolean baja;

    public ResidenteResponseDto(Residente residente) {
        this.id = residente.getId();
        this.nombre = residente.getNombre();
        this.apellido = residente.getApellido();
        this.fechaNacimiento = residente.getFechaNacimiento();
        this.idResidencia = residente.getResidencia().getId();
        this.documentoIdentidad = residente.getDocuemntoIdentidad();
        this.baja = residente.isBaja();
        this.familiar1 = residente.getFamiliar1();
        this.familiar2 = residente.getFamiliar2();
    }
}
