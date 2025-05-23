package com.kevinolarte.resibenissa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String user;

    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "fk_log")
    private LogAccion logPadre;

    @OneToMany(mappedBy = "logPadre")
    private List<LogAccion> hijos = new ArrayList<>();


    public LogAccion(String nombre, String descripcion, String user) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.user = user;
    }

    public LogAccion() {

    }
}
