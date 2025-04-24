package com.kevinolarte.resibenissa.dto.in.moduloOrgSalida;

import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO para representar un evento de salida.
 *<p>
 * Esta clase contiene información básica sobre un evento de salida, incluyendo su ID,
 * nombre, descripción, fecha y estado.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class EventoSalidaDto {
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private EstadoSalida estado;
}
