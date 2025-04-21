package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.RegistroJuego;
import com.kevinolarte.resibenissa.enums.Dificultad;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de salida que representa un registro del uso de un juego por parte de un residente.
 * <p>
 * Este DTO se utiliza para enviar al cliente información sobre una sesión de juego,
 * incluyendo detalles como duración, dificultad, número de errores y fecha de ejecución.
 * También identifica al residente, al juego y al usuario (trabajador) que registró la partida.
 * </p>
 *
 * <p>
 * Si el usuario asociado a la partida es nulo, el campo {@code idUsario} se establece en {@code null}.
 * </p>
 *
 * @author Kevin Olarte.
 */
@Getter
@Setter
public class RegistroJuegoResponseDto {

    private Long id;
    private Long idResidente;
    private Long idJuego;
    private Long idUsuario;
    private Integer num;
    private Double duracion;
    private Dificultad dificultad;
    private LocalDateTime fecha;
    private String observacion;

    public RegistroJuegoResponseDto(RegistroJuego registroJuego) {
        this.id = registroJuego.getId();
        this.idResidente = registroJuego.getResidente().getId();
        this.idJuego = registroJuego.getJuego().getId();
        this.idUsuario = registroJuego.getUsuario() == null? null : registroJuego.getUsuario().getId();
        this.num = registroJuego.getNum();
        this.duracion = registroJuego.getDuracion();
        this.dificultad = registroJuego.getDificultad();
        this.fecha = registroJuego.getFecha();
        this.observacion = registroJuego.getObservacion() == null ? null : registroJuego.getObservacion();
    }
}
