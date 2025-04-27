package com.kevinolarte.resibenissa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entidad que representa a un residente (persona mayor) de una residencia.
 * <p>
 * A diferencia de {@link User}, los residentes no se autentican en el sistema.
 * Esta clase almacena datos personales y permite vincular al residente con su residencia,
 * así como con registros de actividades (como juegos, entrenos, etc).
 * <p>
 * Esta entidad es gestionada por usuarios autenticados del sistema.
 *
 * @author Kevin Olarte
 */
@Entity
@Table(name = "residentes")
@Getter
@Setter
public class Residente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "fecha_nacimiento",nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * documento de identidad del residente.
     * DNI o NIE.
     */
    @Column(name = "documento_identidad", nullable = false, unique = true)
    private String docuemntoIdentidad;

    /**
     * Relación con la residencia donde vive este residente.
     * Múltiples residentes pueden estar en una misma residencia.
     */
    @ManyToOne
    @JoinColumn(name = "fk_residencia", nullable = false)
    private Residencia residencia;

    /**
     * Registros de juegos realizados por este residente.
     * Relación uno a muchos.
     */
    @OneToMany(mappedBy = "residente", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<RegistroJuego> registros = new LinkedHashSet<>();

    /**
     * Conjunto de veces que participa el resudente a las excuirsiones.
     */
    @OneToMany(mappedBy = "residente", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Participante> participantes = new LinkedHashSet<>();

    public Residente(String nombre, String apellido, LocalDate fechaNacimiento, String documentoIdentidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.docuemntoIdentidad = documentoIdentidad;

    }

    public Residente(){

    }

}
