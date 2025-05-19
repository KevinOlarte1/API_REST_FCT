package com.kevinolarte.resibenissa.models.modulojuego;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinolarte.resibenissa.models.Residencia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entidad que representa un juego disponible en una residencia.
 * <p>
 * Cada juego tiene un nombre único dentro de su residencia.
 * Está asociado a múltiples registros que indican cuándo y cómo fue jugado por los residentes.
 *
 * @author Kevin Olarte
 */
@Entity
@Table(
        name = "juegos"

)
@Getter
@Setter
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Nombre del juego. Debe ser único dentro de cada residencia.
     */
    @Column(nullable = false, unique = true)
    private String nombre;


    /**
     * Registros de uso de este juego por parte de residentes.
     */
    @OneToMany(mappedBy = "juego", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<RegistroJuego> registro = new LinkedHashSet<>();

    public Juego(String nombre){
        this.nombre = nombre;
    }
    public Juego(){}


    @Override
    public String toString() {
        return "Juego{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
