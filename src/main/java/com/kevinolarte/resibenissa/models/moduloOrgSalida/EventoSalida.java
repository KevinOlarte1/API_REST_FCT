package com.kevinolarte.resibenissa.models.moduloOrgSalida;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.models.Residencia;
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
    @OneToMany(mappedBy = "salida", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Participante> participantes = new LinkedHashSet<>();

    /**
     * Relación con la residencia donde pueden tener varias salidas.
     * Múltiples Eventos pueden estar en una misma residencia.
     */
    @ManyToOne
    @JoinColumn(name = "fk_residencia")
    private Residencia residencia;

    public EventoSalida(EventoSalidaDto e) {
        this.nombre = e.getNombre();
        this.descripcion = e.getDescripcion();
        this.fechaInicio = e.getFecha();
        this.estado = e.getEstado();
    }
    public EventoSalida() {}
}
