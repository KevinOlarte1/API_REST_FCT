package com.kevinolarte.resibenissa.services.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.EventoSalidaRepository;
import com.kevinolarte.resibenissa.services.ResidenciaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
/**
 * Servicio que gestiona las operaciones relacionadas con los eventos de salida.
 * <p>
 * Permite registrar nuevos eventos, cambiar su estado y consultarlos por su ID.
 * Valida la integridad de los datos antes de realizar las operaciones.
 * </p>
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class EventoSalidaService {

    private final EventoSalidaRepository eventoSalidaRepository;
    private final ResidenciaService residenciaService;


    /**
     * Registra un nuevo evento de salida en la base de datos.
     * <p>
     * Valida que todos los campos obligatorios estén presentes, que la fecha sea válida
     * (no anterior a la actual), que la residencia exista y que el nombre del evento sea único en la residencia.
     * </p>
     *
     * @param input Objeto DTO que contiene los datos del nuevo evento.
     * @return EventoSalidaResponseDto con los datos del evento guardado.
     * @throws ApiException si falta algún campo obligatorio, la fecha es inválida, la residencia no existe
     *                      o el nombre del evento está duplicado en la misma residencia.
     */
    public EventoSalidaResponseDto addEventoSalida(EventoSalidaDto input, Long idResidencia) {
        if (input.getNombre() == null || input.getNombre().trim().isEmpty() || input.getDescripcion() == null || input.getDescripcion().trim().isEmpty()
            || input.getFecha() == null  || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que la fecha no sea anterior a la fecha actual
        if (input.getFecha().isBefore(java.time.LocalDate.now())) {
            throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
        }

        Residencia res = residenciaService.findById(idResidencia);
        if (res == null) {
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        //El nombre de la salida tiene que ser unico en esa misma residencia
        if (eventoSalidaRepository.existsByNombreAndResidenciaId(input.getNombre(), idResidencia)) {
            throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }


        // Crear el evento de salida
        EventoSalida eventoSalida = new EventoSalida(input.getNombre(), input.getDescripcion(), input.getFecha());
        eventoSalida.setResidencia(res);
        EventoSalida newEventoSalida = eventoSalidaRepository.save(eventoSalida);


        return new EventoSalidaResponseDto(newEventoSalida);
    }

    public EventoSalidaResponseDto updateEventoSalida(EventoSalidaDto input,
                                                      Long idResidencia, Long idEventoSalida) {
        if(idResidencia == null || idEventoSalida == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar si existe ese evento
        EventoSalida eventoSalida = eventoSalidaRepository
                .findById(idEventoSalida)
                .orElseThrow(() ->  new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO));

        // Validar si ese evento existe en esa residencia
        if (!eventoSalida.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        //Comprobar si la fecha no es anterior a la actual
        if (input.getFecha() != null && !input.getFecha().isBefore(java.time.LocalDate.now())) {
            eventoSalida.setFechaInicio(input.getFecha());
        }

        if (input.getEstado() != null) {
            eventoSalida.setEstado(input.getEstado());
        }

        return new EventoSalidaResponseDto(eventoSalidaRepository.save(eventoSalida));
    }

    /**
     * Cambiar el estado de un evento de salida.
     * <p>
     *     * Este método permite cambiar el estado de un evento de salida existente.
     * @param idEventoSalida ID del evento de salida a modificar.
     * @param estado Nuevo estado a establecer.
     * @return EventoSalidaResponseDto con los datos del evento de salida actualizado.
     * @throws ApiException si el ID del evento de salida o el estado son nulos, si el evento de salida no existe,
     *          o si la fecha es invalida.
     *
     */
    public EventoSalidaResponseDto changeEstado(Long idEventoSalida, EstadoSalida estado) {
        if (idEventoSalida == null || estado == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaRepository.findById(idEventoSalida).orElse(null);
        if (eventoSalida == null) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }

        // Comprobar la fecha del evento si ya a pasado
        if (eventoSalida.getFechaInicio().isBefore(java.time.LocalDate.now())) {
            throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
        }

        // Validar que el estado no sea el mismo que el actual
        if (eventoSalida.getEstado() == estado) {
            return new EventoSalidaResponseDto(eventoSalida);
        }

        // Cambiar el estado del evento de salida
        eventoSalida.setEstado(estado);
        return new EventoSalidaResponseDto(eventoSalidaRepository.save(eventoSalida));
    }

    public EventoSalidaResponseDto deleteEventoSalida(Long idEventoSalida, Long idResidencia) {
        if (idEventoSalida == null || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaRepository
                .findById(idEventoSalida)
                .orElseThrow(() -> new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO));

        // Validar que el evento de salida pertenece a la residencia
        if (!eventoSalida.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        eventoSalidaRepository.delete(eventoSalida);
        return new EventoSalidaResponseDto(eventoSalida);
    }

    /**
     * Busca un evento de salida por su ID.
     * <p>
     * Este método permite recuperar un evento de salida existente en la base de datos
     * utilizando su ID único.
     * </p>
     *
     * @param id ID del evento de salida a buscar.
     * @return EventoSalida con los datos del evento encontrado, o null si no se encuentra.
     */
    public EventoSalida findById(Long id) {
        return eventoSalidaRepository.findById(id).orElse(null);
    }
}
