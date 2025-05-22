package com.kevinolarte.resibenissa.dto.out.moduloOrgSalida;

import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import jdk.jfr.Event;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida que representa los datos públicos de un evento de salida.
 * <p>
 *   Esta clase se utiliza para enviar al cliente información relevante sobre
 *   un evento de salida sin exponer detalles internos del modelo ni relaciones sensibles.
 *   </p>
 *
 *   Contiene campos como el ID, nombre, descripción, fecha de inicio, estado y lista de participantes.
 *
 *   @author Kevin Olarte
 */
@Getter
@Setter
public class EventoSalidaResponseDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private EstadoSalida estado;
    private List<Long> participantes;
    private Long idResidencia;

    public EventoSalidaResponseDto(EventoSalida e) {
        this.id = e.getId();
        this.nombre = e.getNombre();
        this.descripcion = e.getDescripcion();
        this.fechaInicio = e.getFechaInicio();
        this.estado = e.getEstado();
        this.participantes = e.getParticipantes().stream()
                .map(Participante::getId)
                .toList();
        this.idResidencia = e.getResidencia().getId();
    }
}
