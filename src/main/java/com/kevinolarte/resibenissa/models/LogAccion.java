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
@Getter
@Setter
@Table(name = "log_accion")
public class LogAccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;
    private String correoUser;

    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "fk_log")
    private LogAccion logPadre;

    @OneToMany(mappedBy = "logPadre")
    private List<LogAccion> hijos = new ArrayList<>();


    public LogAccion(String nombre, String descripcion, String correoUser) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.correoUser = correoUser;
    }

    public LogAccion() {

    }
}
