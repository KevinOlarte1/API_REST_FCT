package com.kevinolarte.resibenissa.dto.in.modulojuego;

import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferencia de datos (DTO) utilizado para registrar una partida de juego
 * realizada por un residente en una residencia específica.
 * <p>
 * Incluye los datos mínimos necesarios para asociar el registro con el juego y la residencia,
 * así como la información del desempeño durante la sesión.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class RegistroJuegoDto {

    private Long idJuego;
    private Long idResidente;
    private Long idUsuario;
    private Integer fallos;
    private Double duracion;
    private Dificultad dificultad;
    private String observacion;

    @Override
    public String toString() {
        return "RegistroJuegoDto{" +
                "idJuego=" + idJuego +
                ", idResidente=" + idResidente +
                ", idUsuario=" + idUsuario +
                ", fallos=" + fallos +
                ", duracion=" + duracion +
                ", dificultad=" + dificultad +
                '}';
    }
}


