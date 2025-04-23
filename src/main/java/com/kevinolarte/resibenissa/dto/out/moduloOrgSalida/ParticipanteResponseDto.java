package com.kevinolarte.resibenissa.dto.out.moduloOrgSalida;

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
    private Long idEventoSalida;
    private boolean asistente;
    private String preOpinion;
    private String postOpinion;

    public ParticipanteResponseDto(Participante participante) {
        this.id = participante.getId();
        this.idResidente = participante.getResidente().getId();
        this.idEventoSalida = participante.getSalida().getId();
        this.asistente = participante.isAyuda();
        this.preOpinion = participante.getPreOpinion();
        this.postOpinion = participante.getPostOpinion();
    }
}
