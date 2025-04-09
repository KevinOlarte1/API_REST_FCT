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
 * Cada residencia tiene un identificador, un nombre único y un correo electrónico.
 * Está relacionada con múltiples usuarios del sistema (como cuidadores o administradores),
 * así como con los residentes (personas mayores) que viven en ella.
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

    /**
     * Nombre de la residencia. Debe ser único.
     */
    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * Correo electrónico de contacto de la residencia. También debe ser único.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Usuarios asociados a esta residencia (por ejemplo, personal que la gestiona).
     * Relación uno a muchos.
     */
    @OneToMany(mappedBy = "residencia")
    @JsonIgnore
    private Set<User> usuarios = new LinkedHashSet<>();

    /**
     * Residentes (personas mayores) que viven en esta residencia.
     * Relación uno a muchos.
     */
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
