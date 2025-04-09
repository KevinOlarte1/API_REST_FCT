package com.kevinolarte.resibenissa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "registro_juego")
@Getter
@Setter
public class RegistroJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_residente")
    private Residente residente;

    @ManyToOne
    @JoinColumn(name = "fk_juego")
    private Juego juego;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer fallos;

    @Column(nullable = false)
    private Double duracion;

    public RegistroJuego(Integer fallos, Double duracion){
        this.fallos = fallos;
        this.duracion = duracion;
        this.fecha = LocalDate.now();
    }
    public RegistroJuego(){}
}
