package com.kevinolarte.resibenissa.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Entidad que representa una residencia en la base de datos.
 * <p>
 * Cada residencia tiene un identificador, un nombre, un correo electrónico
 * y una lista de usuarios asociados.
 *
 * @author Kevin Olarte
 */
@Entity
@Table(name = "residencias")
@Getter
@Setter
public class Residencia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "residencia")
    @JsonIgnore
    private Set<User> usuarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "residencia")
    @JsonIgnore
    private Set<Residente> residentes = new LinkedHashSet<>();



    public Residencia(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;

    }

    public Residencia() {

    }
}
