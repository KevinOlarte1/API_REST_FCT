package com.kevinolarte.resibenissa.models.moduloOrgSalida;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representa un evento de salida organizado en el sistema.
 * <p>
 * Cada salida contiene información sobre su nombre, descripción, fecha de inicio y estado.
 * También mantiene una lista de residentes que participan en ella.
 * @author Kevin Olarte
 */
@Entity
@Table(name = "salidas")
@Getter
@Setter
public class EventoSalida {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    /**
     * Nombre del evento de salida.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Descripción del evento, explicando de qué trata o qué actividades incluye.
     */
    @Column(nullable = false)
    private String descripcion;

    /**
     * Fecha en la que se realizará el evento de salida.
     */
    @Column(nullable = false)
    private LocalDate fechaInicio;

    /**
     * Estado actual del evento de salida.
     * <p>
     * Puede ser uno de los valores definidos en el enum {@link EstadoSalida}, como por ejemplo: PENDIENTE, REALIZADA, CANCELADA.
     */
    @Column(nullable = false)
    private EstadoSalida estado;


    /**
     * Conjunto de participantes que asisten a esta salida.
     * <p>
     * Relación uno-a-muchos con {@link Participante}, donde esta salida es la referencia inversa.
     * <p>
     * Se ignora en la serialización JSON para evitar bucles y sobrecarga de datos innecesarios.
     */
    @OneToMany(mappedBy = "salida")
    @JsonIgnore
    private Set<Participante> participantes = new LinkedHashSet<>();

    public EventoSalida(String nombre, String descripcion, LocalDate fechaInicio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.estado = EstadoSalida.ABIERTA;
    }
    public EventoSalida() {}
}
