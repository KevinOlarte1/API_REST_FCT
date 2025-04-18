package com.kevinolarte.resibenissa.models;

import enums.Dificultad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    public RegistroJuego(Integer num, Double duracion, Dificultad dificultad){
        this.num = num;
        this.duracion = duracion;
        this.fecha = LocalDateTime.now();
        this.dificultad = dificultad;
    }
    public RegistroJuego(Double duracion, Dificultad dificultad ){
        this(0,duracion,dificultad);
    }
    public RegistroJuego(Integer num, Double duracion){
        this(num,duracion,null);
    }

    public RegistroJuego() {

    }
}
