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
            Dificultad dificultad,
            Boolean comentado,
            Boolean promedio,
            Boolean masPromedio,
            Boolean menosPromedio
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

            // Filtro por observación
            if (comentado != null) {
                if (comentado) {
                    predicate = cb.and(predicate, cb.isNotNull(root.get("observacion")));
                    predicate = cb.and(predicate, cb.notEqual(cb.trim(cb.literal(' '), root.get("observacion")), ""));
                } else {
                    predicate = cb.and(predicate, cb.or(
                            cb.isNull(root.get("observacion")),
                            cb.equal(cb.trim(cb.literal(' '), root.get("observacion")), "")
                    ));
                }
            }

            // Filtro por media (promedio ±5%, más o menos)
            if ((Boolean.TRUE.equals(promedio) || Boolean.TRUE.equals(masPromedio) || Boolean.TRUE.equals(menosPromedio))
                    && idJuego != null) {

                assert query != null;
                Subquery<Double> subquery = query.subquery(Double.class);
                Root<RegistroJuego> subRoot = subquery.from(RegistroJuego.class);
                subquery.select(cb.avg(subRoot.get("duracion")));



                Expression<Double> mediaExpr = subquery.getSelection();

                if (Boolean.TRUE.equals(promedio)) {
                    Expression<Double> margen = cb.prod(mediaExpr, 0.05); // ±5%
                    Expression<Double> minRango = cb.diff(mediaExpr, margen);
                    Expression<Double> maxRango = cb.sum(mediaExpr, margen);
                    predicate = cb.and(predicate,
                            cb.between(root.get("duracion"), minRango, maxRango));
                } else if (Boolean.TRUE.equals(masPromedio)) {
                    predicate = cb.and(predicate, cb.greaterThan(root.get("duracion"), mediaExpr));
                } else {
                    predicate = cb.and(predicate, cb.lessThan(root.get("duracion"), mediaExpr));
                }
            }

            return predicate;
        };
    }
}
