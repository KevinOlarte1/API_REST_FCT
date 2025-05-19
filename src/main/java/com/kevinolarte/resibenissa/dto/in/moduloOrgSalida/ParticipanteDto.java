package com.kevinolarte.resibenissa.dto.in.moduloOrgSalida;

import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferencia de datos (DTO) utilizado para representar la asistencia de un residente a una salida.
 *
 *  <p>
 *      Esta clase encapsula la información necesaria para identificar al residente y el evento de salida
 *      al que asistió, así como su estado de asistencia.
 *      </p>
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class ParticipanteDto {
    private Long idResidente;
    private Boolean recursosHumanos;
    private Boolean recursosMateriales;
    private String preOpinion;
    private String postOpinion;

}
