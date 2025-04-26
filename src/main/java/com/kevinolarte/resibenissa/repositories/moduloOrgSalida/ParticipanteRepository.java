package com.kevinolarte.resibenissa.repositories.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    @Query("""
        SELECT COUNT(p) > 0
        FROM Participante p
        WHERE p.residente.id = :idResidente
          AND p.salida.fechaInicio = (
              SELECT s.fechaInicio
              FROM EventoSalida s
              WHERE s.id = :idSalida
          )
          AND p.salida.id <> :idSalida
    """)
    boolean existsByResidenteInOtherSalidaSameDay(
            @Param("idResidente") Long idResidente,
            @Param("idSalida") Long idSalida
    );

    Participante findBySalidaAndResidente(EventoSalida eventoSalida, Residente residente);

    List<Participante> id(Long id);

    List<Participante> findBySalida(EventoSalida salida);

    List<Participante> findByAyudaAndSalida(boolean ayuda, EventoSalida salida);
}
