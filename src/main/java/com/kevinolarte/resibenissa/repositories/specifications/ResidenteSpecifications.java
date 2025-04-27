package com.kevinolarte.resibenissa.repositories.specifications;

import com.kevinolarte.resibenissa.models.Residente;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ResidenteSpecifications    {
    public static Specification<Residente> perteneceAResidencia(Long idResidencia) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("residencia").get("id"), idResidencia);
    }

    public static Specification<Residente> documentoIdentidadEquals(String documentoIdentidad) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("docuemntoIdentidad"), documentoIdentidad);
    }

    public static Specification<Residente> fechaNacimientoEquals(LocalDate fechaNacimiento) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("fechaNacimiento"), fechaNacimiento);
    }

    public static Specification<Residente> anioNacimientoEquals(Integer year) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("fechaNacimiento")), year);
    }

    public static Specification<Residente> mesNacimientoEquals(Integer month) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("fechaNacimiento")), month);
    }
}
