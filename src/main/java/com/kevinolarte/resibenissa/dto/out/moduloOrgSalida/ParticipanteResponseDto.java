package com.kevinolarte.resibenissa.dto.out.moduloOrgSalida;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import lombok.Getter;
import lombok.Setter;

/*
 * Objeto de transferencia de datos (DTO) utilizado para representar la respuesta de un participante en un evento de salida.
 * <p>
 * Este DTO contiene información sobre el participante, incluyendo su ID, el ID del residente, el ID del evento de salida,
 * si asistió al evento y sus opiniones antes y después del evento.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class ParticipanteResponseDto {
    private Long id;
    private Long idResidente;
    private Long idEvento;
    private boolean recursosHumanos;
    private boolean recursosMateriales;
    private boolean asistenciaPermitida;
    private String preOpinion;
    private String postOpinion;

    @JsonIgnore
    private Long idResidencia;
    @JsonIgnore
    private String familiar1;
    @JsonIgnore
    private String familiar2;

    public ParticipanteResponseDto(Participante participante) {
        this.id = participante.getId();
        this.idResidente = participante.getResidente().getId();
        this.idEvento = participante.getEvento().getId();
        this.recursosHumanos = participante.isRecursosHumanos();
        this.recursosMateriales = participante.isRecursosMateriales();
        this.preOpinion = participante.getPreOpinion();
        this.postOpinion = participante.getPostOpinion();
        this.asistenciaPermitida = participante.isAsistenciaPermitida();
        this.idResidencia = participante.getResidente().getResidencia().getId();
        this.familiar1 = participante.getResidente().getFamiliar1();
        this.familiar2 = participante.getResidente().getFamiliar2();
    }
}
