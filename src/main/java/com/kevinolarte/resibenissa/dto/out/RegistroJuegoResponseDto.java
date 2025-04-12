package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.RegistroJuego;
import enums.Dificultad;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegistroJuegoResponseDto {

    private Long id;
    private Long idResidente;
    private Long idJuego;
    private Long idUsario;
    private Integer num;
    private Double duracion;
    private Dificultad dificultad;
    private LocalDateTime fecha;

    public RegistroJuegoResponseDto(RegistroJuego registroJuego) {
        this.id = registroJuego.getId();
        this.idResidente = registroJuego.getResidente().getId();
        this.idJuego = registroJuego.getJuego().getId();
        this.idUsario = registroJuego.getUsuario().getId();
        this.num = registroJuego.getNum();
        this.duracion = registroJuego.getDuracion();
        this.dificultad = registroJuego.getDificultad();
        this.fecha = registroJuego.getFecha();
    }
}
