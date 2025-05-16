package com.kevinolarte.resibenissa.specifications;

import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegistroJuegoSpecification {

    public static Specification<RegistroJuego> withDynamicFilters(
            Long idResidencia,
            Long idJuego,
            Long idResidente,
            Integer edad,
            Integer minEdad,
            Integer maxEdad,
            LocalDate fecha,
            LocalDate minFecha,
            LocalDate maxFecha,
            Dificultad dificultad
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtro por idJuego
            if (idJuego != null) {
                predicate = cb.and(predicate, cb.equal(root.get("juego").get("id"), idJuego));
            }

            // Filtro por idResidente
            if (idResidente != null) {
                predicate = cb.and(predicate, cb.equal(root.get("residente").get("id"), idResidente));
            }

            // Filtro por residencia (vía residente)
            if (idResidencia != null) {
                predicate = cb.and(predicate, cb.equal(root.get("residente").get("residencia").get("id"), idResidencia));
            }

            // Filtro por edad
            Path<LocalDate> fechaNacimiento = root.get("residente").get("fechaNacimiento");
            LocalDate hoy = LocalDate.now();

            if (edad != null) {
                LocalDate fechaMax = hoy.minusYears(edad);
                LocalDate fechaMin = hoy.minusYears(edad + 1);
                predicate = cb.and(predicate,
                        cb.between(fechaNacimiento, fechaMin.plusDays(1), fechaMax));
            } else {
                if (minEdad != null) {
                    LocalDate fechaMax = hoy.minusYears(minEdad);
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(fechaNacimiento, fechaMax));
                }
                if (maxEdad != null) {
                    LocalDate fechaMin = hoy.minusYears(maxEdad);
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(fechaNacimiento, fechaMin));
                }
            }

            // Filtro por fecha exacta del registro
            Path<LocalDateTime> fechaRegistro = root.get("fecha");

            if (fecha != null) {
                predicate = cb.and(predicate,
                        cb.between(fechaRegistro,
                                fecha.atStartOfDay(),
                                fecha.plusDays(1).atStartOfDay()));
            } else {
                if (minFecha != null) {
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(fechaRegistro, minFecha.atStartOfDay()));
                }
                if (maxFecha != null) {
                    predicate = cb.and(predicate, cb.lessThan(fechaRegistro, maxFecha.plusDays(1).atStartOfDay()));
                }
            }

            // Filtro por dificultad
            if (dificultad != null) {
                predicate = cb.and(predicate, cb.equal(root.get("dificultad"), dificultad));
            }

            return predicate;
        };
    }
}
