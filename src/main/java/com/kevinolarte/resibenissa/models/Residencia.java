package com.kevinolarte.resibenissa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    private String nombre;
    private String email;

    @OneToMany(mappedBy = "residencia")
    private List<User> usuarios;

}
