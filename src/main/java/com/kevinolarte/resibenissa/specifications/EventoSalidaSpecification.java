package com.kevinolarte.resibenissa.specifications;

import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EventoSalidaSpecification {

    public static Specification<EventoSalida> withDynamicFilters(Long idResidencia,
                                                                 LocalDate fecha,
                                                                 LocalDate minFecha,
                                                                 LocalDate maxFecha,
                                                                 EstadoSalida estado,
                                                                 Long idResidente,
                                                                 Long idParticipante,
                                                                 Integer minRecursosHumanos,
                                                                 Integer maxRecursosHumanos,
                                                                 Integer minRecursosMateriales,
                                                                 Integer maxRecursosMateriales) {
        return (Root<EventoSalida> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtro por residencia
            if (idResidencia != null) {
                predicate = cb.and(predicate, cb.equal(root.get("residencia").get("id"), idResidencia));
            }

            // Filtro por fecha exacta o por rango
            if (fecha != null) {
                predicate = cb.and(predicate, cb.equal(root.get("fechaInicio"), fecha));
            } else {
                if (minFecha != null) {
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("fechaInicio"), minFecha));
                }
                if (maxFecha != null) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("fechaInicio"), maxFecha));
                }
            }

            // Filtro por estado
            if (estado != null) {
                predicate = cb.and(predicate, cb.equal(root.get("estado"), estado));
            }

            // Filtro por participante con idResidente
            if (idResidente != null) {
                Join<Object, Object> participantes = root.join("participantes", JoinType.INNER);
                predicate = cb.and(predicate, cb.equal(participantes.get("residente").get("id"), idResidente));
            }

            // Filtro por idParticipante
            if (idParticipante != null) {
                Join<Object, Object> participantes = root.join("participantes", JoinType.INNER);
                predicate = cb.and(predicate, cb.equal(participantes.get("id"), idParticipante));
            }

            // Subquery para contar recursosHumanos por evento
            if (minRecursosHumanos != null || maxRecursosHumanos != null) {
                Subquery<Long> subRH = query.subquery(Long.class);
                Root<EventoSalida> subRoot = subRH.from(EventoSalida.class);
                Join<Object, Object> subPart = subRoot.join("participantes");
                subRH.select(cb.count(subPart.get("id")));
                subRH.where(
                        cb.equal(subRoot.get("id"), root.get("id")),
                        cb.isTrue(subPart.get("recursosHumanos"))
                );
                if (minRecursosHumanos != null) {
                    predicate = cb.and(predicate, cb.ge(subRH, minRecursosHumanos));
                }
                if (maxRecursosHumanos != null) {
                    predicate = cb.and(predicate, cb.le(subRH, maxRecursosHumanos));
                }
            }

            // Subquery para contar recursosMateriales por evento
            if (minRecursosMateriales != null || maxRecursosMateriales != null) {
                Subquery<Long> subRM = query.subquery(Long.class);
                Root<EventoSalida> subRoot = subRM.from(EventoSalida.class);
                Join<Object, Object> subPart = subRoot.join("participantes");
                subRM.select(cb.count(subPart.get("id")));
                subRM.where(
                        cb.equal(subRoot.get("id"), root.get("id")),
                        cb.isTrue(subPart.get("recursosMateriales"))
                );
                if (minRecursosMateriales != null) {
                    predicate = cb.and(predicate, cb.ge(subRM, minRecursosMateriales));
                }
                if (maxRecursosMateriales != null) {
                    predicate = cb.and(predicate, cb.le(subRM, maxRecursosMateriales));
                }
            }

            return predicate;
        };
    }
}
