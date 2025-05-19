package com.kevinolarte.resibenissa.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Objeto de transferencia de datos (DTO) utilizado para crear o actualizar residentes.
 * <p>
 * Contiene los datos personales básicos necesarios para registrar un residente
 * y asociarlo a una residencia existente.
 * <p>
 * No contiene lógica de negocio ni anotaciones de persistencia.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class ResidenteDto {

    private String nombre;
    private String apellido;

    private LocalDate fechaNacimiento;
    private String documentoIdentidad;
    private String familiar1;
    private String familiar2;
    private Integer year;
    private Integer month;


}
