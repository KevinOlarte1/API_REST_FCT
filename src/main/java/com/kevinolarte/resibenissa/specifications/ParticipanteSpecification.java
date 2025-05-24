package com.kevinolarte.resibenissa.specifications;

import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Especificación para filtrar participantes en eventos de salida.
 *
 */
public class ParticipanteSpecification {

    /**
     * Crea una especificación para filtrar participantes según varios criterios.
     *
     * @param idResidencia ID de la residencia del residente.
     * @param idEvento ID del evento de salida.
     * @param idResidente ID del residente.
     * @param recursosHumanos Indica si se requieren recursos humanos.
     * @param recursosMateriales Indica si se requieren recursos materiales.
     * @param minEdad Edad mínima del residente.
     * @param maxEdad Edad máxima del residente.
     * @param preOpinion Indica si se requiere opinión previa.
     * @param postOpinion Indica si se requiere opinión posterior.
     * @param asistenciaPermitida Indica si la asistencia está permitida.
     * @return Especificación para filtrar participantes.
     */
    public static Specification<Participante> withFilters(
            Long idResidencia,
            Long idEvento,
            Long idResidente,
            Boolean recursosHumanos,
            Boolean recursosMateriales,
            Integer minEdad,
            Integer maxEdad,
            Boolean preOpinion,
            Boolean postOpinion,
            Boolean asistenciaPermitida
    ) {
        return (Root<Participante> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtro por idEvento
            if (idResidencia != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("residente").get("residencia").get("id"), idResidencia));
            }

            // Filtro por idEvento
            if (idEvento != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("evento").get("id"), idEvento));
            }

            // Filtro por idResidente
            if (idResidente != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("residente").get("id"), idResidente));
            }

            // Filtro por recursosHumanos
            if (recursosHumanos != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("recursosHumanos"), recursosHumanos));
            }

            // Filtro por recursosMateriales
            if (recursosMateriales != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("recursosMateriales"), recursosMateriales));
            }

            LocalDate today = LocalDate.now();
            Path<LocalDate> fechaNacimiento = root.get("residente").get("fechaNacimiento");

            // Filtro por edad
            if (minEdad != null) {
                LocalDate maxFechaNac = today.minusYears(minEdad);
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(fechaNacimiento, maxFechaNac));
            }

            // Filtro por edad
            if (maxEdad != null) {
                LocalDate minFechaNac = today.minusYears(maxEdad + 1).plusDays(1);
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(fechaNacimiento, minFechaNac));
            }

            // Filtro por opinion previa
            if (preOpinion != null) {
                if (preOpinion) {
                    predicate = cb.and(predicate,
                            cb.isNotNull(root.get("preOpinion")),
                            cb.notEqual(cb.trim(cb.literal(' '), root.get("preOpinion")), ""));
                } else {
                    predicate = cb.and(predicate, cb.or(
                            cb.isNull(root.get("preOpinion")),
                            cb.equal(cb.trim(cb.literal(' '), root.get("preOpinion")), "")
                    ));
                }
            }

            // Filtro por opinion posterior
            if (postOpinion != null) {
                if (postOpinion) {
                    predicate = cb.and(predicate,
                            cb.isNotNull(root.get("postOpinion")),
                            cb.notEqual(cb.trim(cb.literal(' '), root.get("postOpinion")), ""));
                } else {
                    predicate = cb.and(predicate, cb.or(
                            cb.isNull(root.get("postOpinion")),
                            cb.equal(cb.trim(cb.literal(' '), root.get("postOpinion")), "")
                    ));
                }
            }
            if (asistenciaPermitida != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("asistenciaPermitida"), asistenciaPermitida));
            }


            return predicate;
        };
    }
}
