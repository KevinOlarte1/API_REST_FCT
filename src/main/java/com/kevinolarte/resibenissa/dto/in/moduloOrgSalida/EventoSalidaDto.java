package com.kevinolarte.resibenissa.dto.in.moduloOrgSalida;

import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;

import java.time.LocalDate;

/**
 * DTO para representar un evento de salida.
 * @author Kevin Olarte
 */
public class EventoSalidaDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private EstadoSalida estado;
}
