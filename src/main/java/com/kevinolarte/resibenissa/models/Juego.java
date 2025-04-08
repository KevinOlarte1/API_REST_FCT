package com.kevinolarte.resibenissa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "juego")
@Getter
@Setter
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "fk_residencia")
    private Residencia residencia;

    public Juego(String nombre){
        this.nombre = nombre;
    }
    public Juego(){}

}
