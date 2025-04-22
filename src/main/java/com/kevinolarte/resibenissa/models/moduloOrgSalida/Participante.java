package com.kevinolarte.resibenissa.models.moduloOrgSalida;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinolarte.resibenissa.models.Residente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa la participación de un residente en un evento de salida.
 * <p>
 * Esta entidad contiene información sobre el residente que participa, la salida a la que asiste
 * y otros datos como si necesita ayuda y sus opiniones antes y después del evento.
 * <p>
 * Se asegura que no pueda existir más de un participante con la misma combinación
 * de {@code fk_salida} y {@code fk_residente}, garantizando que un residente no se registre
 * dos veces en la misma salida.
 * @author Kevin Olarte
 */
@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fk_salida", "fk_residente"})
        }
)
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Evento de salida en el que participa el residente.
     * <p>
     * Relación muchos-a-uno con {@link EventoSalida}.
     * Esta relación es ignorada en la serialización JSON para evitar bucles.
     */
    @ManyToOne
    @JoinColumn(name = "fk_salida", nullable = false)
    @JsonIgnore
    private EventoSalida salida;

    /**
     * Residente que participa en la salida.
     * <p>
     * Relación muchos-a-uno con {@link Residente}.
     * Esta relación es ignorada en la serialización JSON para evitar bucles.
     */
    @ManyToOne
    @JoinColumn(name = "fk_residente", nullable = false)
    @JsonIgnore
    private Residente residente;

    /**
     * Indica si el residente requiere ayuda durante la salida.
     */
    @Column(nullable = false)
    private boolean ayuda;

    /**
     * Opinión del residente antes de asistir a la salida (opcional).
     */
    private String preOpinion;


    /**
     * Opinión del residente después de asistir a la salida (opcional).
     */
    private String postOpinion;

    public Participante() {
        this.ayuda = false;
        this.preOpinion = "";
        this.postOpinion = "";
    }





}
