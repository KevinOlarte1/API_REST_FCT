package com.kevinolarte.resibenissa.services.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.EventoSalidaRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import com.kevinolarte.resibenissa.services.ResidenciaService;
import com.kevinolarte.resibenissa.services.ResidenteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las operaciones relacionadas con los eventos de salida.
 * <p>
 * Permite registrar nuevos eventos, actualizar información, cambiar su estado,
 * eliminar eventos y realizar consultas personalizadas sobre eventos de salida asociados a una residencia.
 * </p>
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class EventoSalidaService {

    private final EventoSalidaRepository eventoSalidaRepository;
    private final ResidenciaService residenciaService;
    private final ResidenteService residenteService;
    private final ParticipanteRepository participanteRepository;


    /**
     * Registra un nuevo evento de salida en una residencia específica.
     *
     * @param input Objeto DTO que contiene los datos del nuevo evento.
     * @param idResidencia ID de la residencia donde se creará el evento.
     * @return DTO con los datos del evento guardado.
     * @throws ApiException si falta algún campo obligatorio, la fecha es inválida,
     *                      la residencia no existe o el nombre ya está en uso en esa residencia.
     */
    public EventoSalidaResponseDto add(EventoSalidaDto input, Long idResidencia) {
        if (input.getNombre() == null || input.getNombre().trim().isEmpty() || input.getDescripcion() == null || input.getDescripcion().trim().isEmpty()
            || input.getFecha() == null  || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        Residencia res = residenciaService.findById(idResidencia);
        if (res == null) {
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        // Validar que la fecha no sea anterior a la fecha actual
        if (input.getFecha().isBefore(java.time.LocalDate.now())) {
            throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
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

    /**
     * Actualiza los datos de un evento de salida existente.
     * <p>
     * Permite modificar únicamente la fecha y el estado del evento.
     * </p>
     *
     * @param input DTO con los datos actualizados.
     * @param idResidencia ID de la residencia del evento.
     * @param idEventoSalida ID del evento a actualizar.
     * @return DTO con los datos del evento actualizado.
     * @throws ApiException si los IDs son inválidos, el evento no existe o no pertenece a la residencia.
     */
    public EventoSalidaResponseDto update(EventoSalidaDto input,
                                                      Long idResidencia, Long idEventoSalida) {

        EventoSalida eventoSalida = validarEventoSalida(idResidencia, idEventoSalida);

        // Validar que la fecha no sea anterior a la fecha actual
        if (input.getFecha() != null && input.getFecha().isBefore(java.time.LocalDate.now())) {
            throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
        }

        //Comprobar si la fecha no es anterior a la actual
        if (input.getFecha() != null) {
            eventoSalida.setFechaInicio(input.getFecha());
        }

        if (input.getEstado() != null) {
            eventoSalida.setEstado(input.getEstado());
        }
        if (input.getNombre() != null) {
            // Comprobar si ya existe un evento de salida con ese nombre en esa residencia
            EventoSalida eventoSalida1 = eventoSalidaRepository.findByNombreAndResidencia_Id(input.getNombre(), idResidencia);
            if (eventoSalida1 !=null && !eventoSalida1.getId().equals(idEventoSalida)) {
                throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
            }
            eventoSalida.setNombre(input.getNombre());
        }
        if (input.getDescripcion() != null) {
            eventoSalida.setDescripcion(input.getDescripcion());
        }

        return new EventoSalidaResponseDto(eventoSalidaRepository.save(eventoSalida));
    }


    /**
     * Elimina un evento de salida de la residencia.
     *
     * @param idEventoSalida ID del evento de salida a eliminar.
     * @param idResidencia ID de la residencia asociada.
     * @throws ApiException si el evento no existe o no pertenece a la residencia.
     */
    public void delete(Long idEventoSalida, Long idResidencia) {
        EventoSalida eventoSalida = validarEventoSalida(idEventoSalida, idResidencia);

        eventoSalidaRepository.delete(eventoSalida);
    }

    /**
     * Recupera un evento de salida por su ID.
     *
     * @param id ID del evento de salida a buscar.
     * @return EventoSalida encontrado, o null si no existe.
     */
    public EventoSalida findById(Long id) {
        return eventoSalidaRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene un evento de salida específico por su ID y residencia.
     *
     * @param idEventoSalida ID del evento de salida.
     * @param idResidencia ID de la residencia asociada.
     * @return DTO del evento de salida encontrado.
     * @throws ApiException si el evento no existe o no pertenece a la residencia.
     */
    public EventoSalidaResponseDto get(Long idEventoSalida, Long idResidencia) {
       EventoSalida eventoSalida = validarEventoSalida(idEventoSalida, idResidencia);
        return new EventoSalidaResponseDto(eventoSalida);
    }


    public List<EventoSalidaResponseDto> getAll(Long idResidencia, LocalDate fecha,
                                                Integer year, Integer month,
                                                EstadoSalida estado, Long idResidente, Long idParticipante) {
        if (idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que la residencia existe
        Residencia res = residenciaService.findById(idResidencia);
        if (res == null) {
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        //Validar fechas
        if (fecha != null){
            if (fecha.isBefore(java.time.LocalDate.now())) {
                throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
            }
        }else {
            if (year != null)
                if (year < 0)
                    throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
            if (month != null)
                if (month < 1 || month > 12)
                    throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
        }

        //No se puede filtrar por residente y participante a la vez
        if (idResidente != null && idParticipante != null) {
            throw new ApiException(ApiErrorCode.CONFLICTO_REFERENCIAS);
        }

        //Comprobar si el residente existe
        Residente residente = null;
        if (idResidente != null) {
            residente = residenteService.findById(idResidente);
            if (residente == null) {
                throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
            }
            if (!residente.getResidencia().getId().equals(idResidencia)) {
                throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
            }
        }
        //Comprobar si el participante existe
        Participante participante = null;
        if (idParticipante != null) {
            participante = participanteRepository.findById(idParticipante)
                    .orElseThrow(() -> new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO));

            if (!participante.getSalida().getResidencia().getId().equals(idResidencia)) {
                throw new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO);
            }
        }
        // Obtener todos los eventos de salida de la residencia
        List<EventoSalida> baseList = eventoSalidaRepository.findByResidenciaId(idResidencia);
        if( fecha != null){
            baseList = baseList.stream()
                    .filter(eventoSalida -> {
                        boolean match = true;
                        match = eventoSalida.getFechaInicio().isEqual(fecha);
                        if (idParticipante != null) match = eventoSalida.getParticipantes().stream()
                                .anyMatch(participante1 -> participante1.getId().equals(idParticipante));

                        if(idResidente != null) match =eventoSalida.getParticipantes().stream()
                                .anyMatch(participant ->
                                        participant.getResidente().getId().equals(idResidente));
                        if(estado != null) match = eventoSalida.getEstado().equals(estado);
                        return match;

                    }).toList();
        }
        else if (year != null || month != null){
            baseList = baseList.stream()
                    .filter(eventoSalida -> {
                        boolean match = true;
                        if (year != null) match = eventoSalida.getFechaInicio().getYear() == year;
                        if (month != null) match = eventoSalida.getFechaInicio().getMonthValue() == month;
                        if (idParticipante != null) match = eventoSalida.getParticipantes().stream()
                                .anyMatch(participante1 -> participante1.getId().equals(idParticipante));

                        if(idResidente != null) match =eventoSalida.getParticipantes().stream()
                                .anyMatch(participant ->
                                        participant.getResidente().getId().equals(idResidente));
                        if(estado != null) match = eventoSalida.getEstado().equals(estado);
                        return match;

                    }).toList();
        }



        return baseList.stream()
                .map(EventoSalidaResponseDto::new)
                .toList();

    }

    /**
     * Valida que un evento de salida pertenezca a una residencia específica.
     *
     * @param idEventoSalida ID del evento de salida.
     * @param idResidencia ID de la residencia.
     * @return EventoSalida validado.
     * @throws ApiException si el evento no existe o no pertenece a la residencia.
     */
    private EventoSalida validarEventoSalida(Long idEventoSalida, Long idResidencia) {
        if (idEventoSalida == null || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaRepository
                .findById(idEventoSalida)
                .orElseThrow(() -> new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO));;

        // Validar que el evento de salida pertenece a la residencia
        if (!eventoSalida.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }

        return eventoSalida;
    }

}
