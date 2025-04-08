package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.Residencia;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResidenciaRepository extends CrudRepository<Residencia, Long> {

    Optional<Residencia> findByEmail(String email);
    Optional<Residencia> findByNombre(String nombre);
}
