package com.kevinolarte.resibenissa.specifications;

import com.kevinolarte.resibenissa.models.Residente;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ResidenteSpecification {

    /**
     * Crea una especificación para filtrar residentes según varios criterios.
     *
     * @param idResidencia ID de la residencia del residente.
     * @param fechaNacimiento Fecha de nacimiento exacta del residente.
     * @param minFechaNacimiento Fecha mínima de nacimiento del residente.
     * @param maxFechaNacimiento Fecha máxima de nacimiento del residente.
     * @param minAge Edad mínima del residente (se calcula como fecha).
     * @param maxAge Edad máxima del residente (se calcula como fecha).
     * @param idJuego ID del juego en el que el residente tiene registros.
     * @param idEvento ID del evento en el que el residente ha participado.
     * @return Especificación para filtrar residentes.
     */
    public static Specification<Residente> withFilters(
            Long idResidencia,
            LocalDate fechaNacimiento,
            LocalDate minFechaNacimiento,
            LocalDate maxFechaNacimiento,
            Integer minAge,
            Integer maxAge,
            Long idJuego,
            Long idEvento
    ) {
        return (Root<Residente> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtro fijo: baja = false
            predicate = cb.and(predicate, cb.isFalse(root.get("baja")));

            // Filtro por residencia
            if (idResidencia != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("residencia").get("id"), idResidencia));
            }

            Path<LocalDate> fechaNacPath = root.get("fechaNacimiento");
            LocalDate today = LocalDate.now();

            // Fecha exacta
            if (fechaNacimiento != null) {
                predicate = cb.and(predicate, cb.equal(fechaNacPath, fechaNacimiento));
            } else {
                if (minFechaNacimiento != null) {
                    predicate = cb.and(predicate,
                            cb.greaterThanOrEqualTo(fechaNacPath, minFechaNacimiento));
                }
                if (maxFechaNacimiento != null) {
                    predicate = cb.and(predicate,
                            cb.lessThanOrEqualTo(fechaNacPath, maxFechaNacimiento));
                }
            }

            // Edad → transformada a fecha
            if (minAge != null) {
                LocalDate fechaMax = today.minusYears(minAge);
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(fechaNacPath, fechaMax));
            }
            if (maxAge != null) {
                LocalDate fechaMin = today.minusYears(maxAge + 1).plusDays(1);
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(fechaNacPath, fechaMin));
            }

            // Filtro por juego (si tiene registros en ese juego)
            if (idJuego != null) {
                Join<Object, Object> registrosJoin = root.join("registros", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.equal(registrosJoin.get("juego").get("id"), idJuego));
                query.distinct(true);
            }

            // Filtro por evento (si ha participado en ese evento)
            if (idEvento != null) {
                Join<Object, Object> participantesJoin = root.join("participantes", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.equal(participantesJoin.get("evento").get("id"), idEvento));
                query.distinct(true);
            }

            return predicate;
        };
    }

    /**
     * Crea una especificación para filtrar residentes dados de baja.
     *
     * @param fechaBaja Fecha de baja exacta del residente.
     * @param minFechaBaja Fecha mínima de baja del residente.
     * @param maxFechaBaja Fecha máxima de baja del residente.
     * @param idResidencia ID de la residencia del residente.
     * @return Especificación para filtrar residentes dados de baja.
     */
    public static Specification<Residente> withFiltersBaja(
            LocalDate fechaBaja,
            LocalDate minFechaBaja,
            LocalDate maxFechaBaja,
            Long idResidencia
    ) {
        return (Root<Residente> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtro fijo: residentes dados de baja
            predicate = cb.and(predicate, cb.isTrue(root.get("baja")));

            // Filtro por fecha de baja
            Path<LocalDate> fechaBajaPath = root.get("fechaBaja");

            if (fechaBaja != null) {
                predicate = cb.and(predicate, cb.equal(fechaBajaPath, fechaBaja));
            } else {
                if (minFechaBaja != null) {
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(fechaBajaPath, minFechaBaja));
                }
                if (maxFechaBaja != null) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(fechaBajaPath, maxFechaBaja));
                }
            }

            // Filtro opcional por residencia
            if (idResidencia != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("residencia").get("id"), idResidencia));
            }

            return predicate;
        };
    }



}
