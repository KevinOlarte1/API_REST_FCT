package com.kevinolarte.resibenissa.specifications;

import com.kevinolarte.resibenissa.models.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Especificaciones para filtrar usuarios en base a diferentes criterios.
 */
public class UserSpecification {


    /**
     * Crea una especificación para filtrar usuarios según varios criterios.
     *
     * @param enabled       Estado de habilitación del usuario (null para no filtrar).
     * @param idResidencia ID de la residencia del usuario (null para no filtrar).
     * @param idJuego      ID del juego en el que el usuario ha registrado partidas (null para no filtrar).
     * @return Especificación que puede ser utilizada en consultas JPA.
     */
    public static Specification<User> withFilters(Boolean enabled, Long idResidencia, Long idJuego) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.isFalse(root.get("baja")));

            // Filtro por estado "enabled"
            if (enabled != null) {
                predicate = cb.and(predicate, cb.equal(root.get("enabled"), enabled));
            }

            // Filtro por residencia
            if (idResidencia != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("residencia").get("id"), idResidencia));
            }

            // Filtro por juego (solo si ha registrado partidas en ese juego)
            if (idJuego != null) {
                Join<Object, Object> registroJoin = root.join("registroJuegos", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.equal(registroJoin.get("juego").get("id"), idJuego));
                assert query != null;
                query.distinct(true); // evita duplicados
            }

            return predicate;
        };

    }

    /**
     * Crea una especificación para filtrar usuarios dados de baja según varios criterios.
     *
     * @param fecha        Fecha exacta de baja (null para no filtrar por fecha exacta).
     * @param minFecha     Fecha mínima de baja (null para no filtrar por fecha mínima).
     * @param maxFecha     Fecha máxima de baja (null para no filtrar por fecha máxima).
     * @param idResidencia ID de la residencia del usuario (null para no filtrar por residencia).
     * @return Especificación que puede ser utilizada en consultas JPA.
     */
    public static Specification<User> withFiltersBaja(LocalDate fecha, LocalDate minFecha, LocalDate maxFecha, Long idResidencia) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtro fijo: usuarios dados de baja
            predicate = cb.and(predicate, cb.isTrue(root.get("baja")));

            // Filtro por fecha exacta de baja
            if (fecha != null) {
                predicate = cb.and(predicate, cb.equal(root.get("fechaBaja"), fecha));
            } else {
                if (minFecha != null) {
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("fechaBaja"), minFecha));
                }
                if (maxFecha != null) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("fechaBaja"), maxFecha));
                }
            }

            // Filtro por residencia
            if (idResidencia != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("residencia").get("id"), idResidencia));
            }

            return predicate;
        };
    }

}
