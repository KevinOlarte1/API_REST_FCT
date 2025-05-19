package com.kevinolarte.resibenissa.repositories.moduloOrgSalida;

import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

import java.util.List;

@Repository
public interface EventoSalidaRepository extends JpaRepository<EventoSalida, Long>, JpaSpecificationExecutor<EventoSalida> {

    /**
     * Exsite un nombre igual en la misma residencia
     */
    boolean existsByNombreAndResidenciaId(String nombre, Long residenciaId);

    /**
     * Elimina todos los eventos de salida asociados a una residencia.
     * @param idResidencia ID de la residencia cuyos eventos de salida
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM EventoSalida p WHERE p.residencia.id = :idResidencia")
    void deleteAllByResidenciaId(@Param("idResidencia") Long idResidencia);


    /**
     * Consulta todos los eventos de salida de una residencia.
     * @param idResidencia ID de la residencia a la que pertenecen los eventos de salida.
     * @return Una lista de eventos de salida que pertenecen a la residencia especificada.
     */
    List<EventoSalida> findByResidenciaId(Long idResidencia);


    EventoSalida findByNombreAndResidencia_Id(String nombre, Long residenciaId);
}
