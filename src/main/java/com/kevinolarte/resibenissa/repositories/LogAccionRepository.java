package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.LogAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogAccionRepository extends JpaRepository<LogAccion, Long> {

    /**
     * Busca registros de acción por el ID del registro padre.
     *
     * @param idPadre ID del registro padre.
     * @return Lista de registros de acción que tienen el registro padre especificado.
     */
    @Query("SELECT l FROM LogAccion l WHERE l.logPadre.id = :idPadre")
    List<LogAccion> findByPadre(Long idPadre);




}
