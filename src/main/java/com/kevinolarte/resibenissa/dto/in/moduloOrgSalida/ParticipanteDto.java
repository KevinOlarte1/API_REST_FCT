package com.kevinolarte.resibenissa.dto.in.moduloOrgSalida;

import lombok.Getter;

/**
 * Objeto de transferencia de datos (DTO) utilizado para representar la asistencia de un residente a una salida.
 * @author Kevin Olarte
 */
public class ParticipanteDto {
    private Long idResidente;
    private Long idEventoSalida;
    private boolean asistencia;

}
