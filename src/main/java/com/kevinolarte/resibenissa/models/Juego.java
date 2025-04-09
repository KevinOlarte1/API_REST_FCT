package com.kevinolarte.resibenissa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "juego",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nombre", "fk_residencia"})
        }
)
@Getter
@Setter
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "fk_residencia", nullable = false)
    private Residencia residencia;

    @OneToMany(mappedBy = "residente")
    private Set<RegistroJuego> registro = new LinkedHashSet<>();

    public Juego(String nombre){
        this.nombre = nombre;
    }
    public Juego(){}

}
