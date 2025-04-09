package com.kevinolarte.resibenissa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    @ManyToOne
    @JoinColumn(name = "fk_juego")
    private Juego juego;

    /**
     * Fecha en la que se jugó.
     */
    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * Número de fallos cometidos por el residente durante el juego.
     */
    @Column(nullable = false)
    private Integer fallos;

    /**
     * Duración del juego en segundos (o minutos, según convención del sistema).
     */
    @Column(nullable = false)
    private Double duracion;

    public RegistroJuego(Integer fallos, Double duracion){
        this.fallos = fallos;
        this.duracion = duracion;
        this.fecha = LocalDate.now();
    }
    public RegistroJuego(){}
}
