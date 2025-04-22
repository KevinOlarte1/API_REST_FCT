package com.kevinolarte.resibenissa.dto.out.modulojuego;

import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida que representa los datos públicos de un juego registrado.
 * <p>
 * Esta clase es utilizada para enviar al cliente información sobre un juego,
 * como su identificador, nombre y la residencia a la que pertenece.
 * </p>
 *
 * Se construye a partir de una entidad {@link Juego}.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class JuegoResponseDto {
    private Long id;
    private String nombre;
    private Long idResidencia;

    public JuegoResponseDto(Juego juego){
        this.id = juego.getId();
        this.nombre = juego.getNombre();
        this.idResidencia = juego.getResidencia().getId();
    }
}
