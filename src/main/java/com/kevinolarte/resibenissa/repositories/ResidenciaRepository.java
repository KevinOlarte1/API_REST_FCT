package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.Residencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, Long> {

    Optional<Residencia> findByEmail(String email);
    Optional<Residencia> findByNombre(String nombre);
}
