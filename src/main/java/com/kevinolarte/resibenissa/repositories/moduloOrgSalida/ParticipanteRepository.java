package com.kevinolarte.resibenissa.repositories.moduloOrgSalida;


import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long>, JpaSpecificationExecutor<Participante> {

    @Query("""
        SELECT COUNT(p) > 0
        FROM Participante p
        WHERE p.residente.id = :idResidente
          AND p.evento.fechaInicio = (
              SELECT s.fechaInicio
              FROM EventoSalida s
              WHERE s.id = :idEvento
          )
          AND p.evento.id <> :idEvento
    """)
    boolean existsByResidenteInOtherEventoSameDay(
            @Param("idResidente") Long idResidente,
            @Param("idEvento") Long idEvento
    );

    /**
     * Elimina todos los participantes asociados a una residencia específica.
     *
     * @param idResidencia ID de la residencia.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Participante p WHERE p.residente.residencia.id = :idResidencia")
    void deleteAllByResidenciaId(@Param("idResidencia") Long idResidencia);

    List<Participante> id(Long id);

}