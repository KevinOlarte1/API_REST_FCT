package com.kevinolarte.resibenissa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa un registro de acción en el sistema.
 * <p>
 *     Esta clase almacena información sobre acciones realizadas por los usuarios,
 *     incluyendo el nombre de la acción, una descripción, el correo del usuario que la realizó,ç
 *     y la fecha de la acción.
 *     <p>
 * @Author    Kevin Olarte
 */
@Entity
@Table(name = "logger")
@Getter
@Setter
public class Logger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;
    private String metodo;
    private String descripcion;
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "id_padre")
    private Logger padre;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL)
    private List<Logger> hijos = new ArrayList<>();

    // Constructores
    public Logger() {}

    public Logger(String endpoint, String metodo, String descripcion) {
        this.endpoint = endpoint;
        this.metodo = metodo;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }

    // Getters y setters
}