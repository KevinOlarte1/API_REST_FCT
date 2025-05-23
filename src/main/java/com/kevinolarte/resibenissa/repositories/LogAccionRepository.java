package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.LogAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogAccionRepository extends JpaRepository<LogAccion, Long> {

    @Query("SELECT l FROM LogAccion l WHERE l.logPadre.id = :idPadre")
    List<LogAccion> findByPadre(Long idPadre);




}
