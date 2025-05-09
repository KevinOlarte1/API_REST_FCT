package com.kevinolarte.resibenissa.repositories.moduloOrgSalida;

import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

import java.util.List;

@Repository
public interface EventoSalidaRepository extends JpaRepository<EventoSalida, Long> {

    /**
     * Exsite un nombre igual en la misma residencia
     */
    boolean existsByNombreAndResidenciaId(String nombre, Long residenciaId);


    /**
     * Consulta un evento de salida por su ID y el ID de la residencia.
     * @param idEventoSalida ID del evento de salida a buscar.
     * @param idResidencia ID de la residencia a la que pertenece el evento.
     * @return Una lista de eventos de salida que coinciden con los IDs proporcionados.
     */
    List<EventoSalida> findByIdAndResidenciaId(Long idEventoSalida, Long idResidencia);

    /**
     * Consulta todos los eventos de salida de una residencia.
     * @param idResidencia ID de la residencia a la que pertenecen los eventos de salida.
     * @return Una lista de eventos de salida que pertenecen a la residencia especificada.
     */
    List<EventoSalida> findByResidenciaId(Long idResidencia);

    /**
     * Consulta eventos de salida por fecha de inicio y residencia.
     * @param fechaInicio Fecha de inicio del evento de salida.
     * @param residenciaId ID de la residencia a la que pertenece el evento.
     * @return Una lista de eventos de salida que coinciden con la fecha y residencia especificadas.
     */
    List<EventoSalida> findByFechaInicioAndResidenciaId(LocalDate fechaInicio, Long residenciaId);

    /**
     * Consulta eventos de salida por estado y residencia.
     * @param estado Estado del evento de salida.
     * @param residenciaId ID de la residencia a la que pertenece el evento.
     * @return Una lista de eventos de salida que coinciden con el estado y residencia especificados.
     */
    List<EventoSalida> findByEstadoAndResidenciaId(EstadoSalida estado, Long residenciaId);

    /**
     * Consulta eventos de salida por fecha de inicio, estado y residencia.
     * @param fechaInicio Fecha de inicio del evento de salida.
     * @param estado Estado del evento de salida.
     * @param residencia_id ID de la residencia a la que pertenece el evento.
     * @return Una lista de eventos de salida que coinciden con la fecha, estado y residencia especificados.
     */
    List<EventoSalida> findByFechaInicioAndEstadoAndResidenciaId(LocalDate fechaInicio, EstadoSalida estado, Long residencia_id);

    EventoSalida findByNombreAndResidencia_Id(String nombre, Long residenciaId);
}
