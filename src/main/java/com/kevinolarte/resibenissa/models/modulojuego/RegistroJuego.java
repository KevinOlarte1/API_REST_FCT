package com.kevinolarte.resibenissa.models.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un registro del uso de un juego por parte de un residente.
 * <p>
 * Cada vez que un residente juega, se guarda un registro con la fecha, la duración
 * del juego y el número de fallos cometidos.
 * Este historial puede ser usado para analizar la evolución cognitiva o motriz del residente.
 *
 * @author Kevin Olarte
 */
@Entity
@Table(name = "registro_juego")
@Getter
@Setter
public class RegistroJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Residente que ha jugado.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_residente")
    private Residente residente;

    /**
     * Juego que fue utilizado.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_juego")
    private Juego juego;

    /**
     * Trabajador que registra la partida.
     */
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private User usuario;

    /**
     * Fecha en la que se jugó.
     */
    @Column(nullable = false)
    private LocalDateTime fecha;

    /**
     * Número de fallos cometidos por el residente durante el juego.
     */
    private Integer num;

    /**
     * Duración del juego en segundos (o minutos, según convención del sistema).
     */
    @Column(nullable = false)
    private Double duracion;

    private Dificultad dificultad;
    private String observacion;

    public RegistroJuego(RegistroJuegoDto input) {
        this.num = input.getNum();
        this.duracion = input.getDuracion();
        this.fecha = LocalDateTime.now();
        this.dificultad = input.getDificultad();
        this.observacion = "";
    }

    public RegistroJuego() {

    }
}
