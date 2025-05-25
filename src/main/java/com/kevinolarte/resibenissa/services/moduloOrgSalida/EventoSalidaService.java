package com.kevinolarte.resibenissa.services.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.EventoSalidaRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import com.kevinolarte.resibenissa.services.ResidenciaService;
import com.kevinolarte.resibenissa.services.ResidenteService;
import com.kevinolarte.resibenissa.specifications.EventoSalidaSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private final static EstadoSalida[] secuenciaEstados =
            {EstadoSalida.CERRADO, EstadoSalida.EN_CURSO,EstadoSalida.FINALIZADA, EstadoSalida.ABIERTO};


    /**
     * Registra un nuevo evento de salida en una residencia específica.
     *
     * @param input Objeto DTO que contiene los datos del nuevo evento.
     * @param idResidencia ID de la residencia donde se creará el evento.
     * @return DTO con los datos del evento guardado.
     * @throws ResiException si falta algún campo obligatorio, la fecha es inválida,
     *                      la residencia no existe o el nombre ya está en uso en esa residencia.
     */
    public EventoSalidaResponseDto add(EventoSalidaDto input, Long idResidencia) {
        if (input.getNombre() == null || input.getNombre().trim().isEmpty() || input.getDescripcion() == null || input.getDescripcion().trim().isEmpty()
            || input.getFecha() == null || idResidencia == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        Residencia res = residenciaService.getResidencia(idResidencia);

        // Validar que la fecha no sea anterior a la fecha actual
        if (input.getFecha().isBefore(LocalDateTime.now())) {
            throw new ResiException(ApiErrorCode.FECHA_INVALIDO);
        }



        //El nombre de la salida tiene que ser unico en esa misma residencia
        if (eventoSalidaRepository.existsByNombreAndResidenciaId(input.getNombre(), idResidencia)) {
            throw new ResiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }


        // Crear el evento de salida
        EventoSalida eventoSalida = new EventoSalida(input);
        eventoSalida.setResidencia(res);
        EventoSalida newEventoSalida = eventoSalidaRepository.save(eventoSalida);


        return new EventoSalidaResponseDto(newEventoSalida);
    }





    /**
     * Obtiene un evento de salida específico por su ID y residencia.
     *
     * @param idEventoSalida ID del evento de salida.
     * @param idResidencia ID de la residencia asociada.
     * @return DTO del evento de salida encontrado.
     * @throws ResiException si el evento no existe o no pertenece a la residencia.
     */
    public EventoSalidaResponseDto get(Long idEventoSalida, Long idResidencia) {
        EventoSalida eventoSalida = getEventoSalida(idResidencia, idEventoSalida);
        return new EventoSalidaResponseDto(eventoSalida);
    }


    public List<EventoSalidaResponseDto> getAll(Long idResidencia, LocalDate fecha,
                                                LocalDate minFecha, LocalDate maxFecha,
                                                EstadoSalida estado, Long idResidente,
                                                Long idParticipante,  Integer minRH,
                                                Integer maxRH, Integer minRM,
                                                Integer maxRM) {
        if (idResidencia == null ){
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        List<EventoSalida> eventos = eventoSalidaRepository.findAll(
                EventoSalidaSpecification.withDynamicFilters(
                        idResidencia, fecha, minFecha, maxFecha, estado,
                        idResidente, idParticipante,
                        minRH, maxRH,
                        minRM, maxRM
                )
        );




        return eventos.stream()
                .map(EventoSalidaResponseDto::new)
                .toList();

    }

    public List<EventoSalidaResponseDto> getAll( LocalDate fecha,
                                                 LocalDate minFecha, LocalDate maxFecha,
                                                 EstadoSalida estado, Long idResidente,
                                                 Long idParticipante,  Integer minRH,
                                                 Integer maxRH, Integer minRM,
                                                 Integer maxRM) {


        List<EventoSalida> eventos = eventoSalidaRepository.findAll(
                EventoSalidaSpecification.withDynamicFilters(
                        null, fecha, minFecha, maxFecha, estado,
                        idResidente, idParticipante,
                        minRH, maxRH,
                        minRM, maxRM
                )
        );




        return eventos.stream()
                .map(EventoSalidaResponseDto::new)
                .toList();

    }





    /**
     * Elimina un evento de salida de la residencia.
     *
     * @param idEventoSalida ID del evento de salida a eliminar.
     * @param idResidencia ID de la residencia asociada.
     * @throws ResiException si el evento no existe o no pertenece a la residencia.
     */
    public void delete(Long idEventoSalida, Long idResidencia) {
        EventoSalida eventoSalida = getEventoSalida(idResidencia, idEventoSalida);

        eventoSalidaRepository.delete(eventoSalida);
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
     * @throws ResiException si los IDs son inválidos, el evento no existe o no pertenece a la residencia.
     */
    public EventoSalidaResponseDto update(EventoSalidaDto input, Long idResidencia, Long idEventoSalida) {

        EventoSalida eventoSalida = getEventoSalida(idResidencia, idEventoSalida);


        if (input.getNombre() != null) {
            // Comprobar si ya existe un evento de salida con ese nombre en esa residencia
            EventoSalida eventoSalida1 = eventoSalidaRepository.findByNombreAndResidencia_Id(input.getNombre(), idResidencia);
            if (eventoSalida1 !=null && !eventoSalida1.getId().equals(idEventoSalida)) {
                throw new ResiException(ApiErrorCode.NOMBRE_DUPLICADO);
            }
            eventoSalida.setNombre(input.getNombre());
        }
        if (input.getDescripcion() != null) {
            eventoSalida.setDescripcion(input.getDescripcion());
        }
        if (input.getFecha() != null) {
            // Validar que la fecha no sea anterior a la fecha actual
            if (input.getFecha().isBefore(LocalDateTime.now())) {
                throw new ResiException(ApiErrorCode.FECHA_INVALIDO);
            }
            eventoSalida.setFechaInicio(input.getFecha());
        }

        if (input.getEstado() != null){
            if(!secuenciaEstados[eventoSalida.getEstado().getEstado()].equals(input.getEstado()))
                throw new ResiException(ApiErrorCode.ESTADO_INVALIDO);
            eventoSalida.setEstado(input.getEstado());
        }




        return new EventoSalidaResponseDto(eventoSalidaRepository.save(eventoSalida));
    }

    /**
     * Cambia el estado de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a modificar.
     * @param estado Nuevo estado a asignar al evento.
     * @param idResidencia ID de la residencia asociada al evento.
     * @return DTO con los datos del evento actualizado.
     * @throws ResiException si el estado es nulo, el evento no existe o el estado no es válido para la transición actual.
     */
    public EventoSalidaResponseDto changeEstado(Long idEventoSalida, EstadoSalida estado, Long idResidencia) {
        if (estado == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        EventoSalida evento = getEventoSalida(idResidencia,idEventoSalida);

        if(!secuenciaEstados[evento.getEstado().getEstado()].equals(estado)){
            throw new ResiException(ApiErrorCode.ESTADO_INVALIDO);
        }
        evento.setEstado(estado);
        return new EventoSalidaResponseDto(eventoSalidaRepository.save(evento));
    }

    /**
     * Cambia la fecha de inicio de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a modificar.
     * @param fecha Nueva fecha de inicio del evento.
     * @param idResidencia ID de la residencia asociada al evento.
     * @return DTO con los datos del evento actualizado.
     * @throws ResiException si la fecha es nula, el evento no existe o la fecha es inválida (anterior a la actual).
     */
    public EventoSalidaResponseDto changeFecha(Long idEventoSalida, LocalDateTime fecha, Long idResidencia) {
        if (fecha == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        EventoSalida evento = getEventoSalida(idResidencia,idEventoSalida);

        // Validar que la fecha no sea anterior a la fecha actual
        if (fecha.isBefore(LocalDateTime.now())) {
            throw new ResiException(ApiErrorCode.FECHA_INVALIDO);
        }

        evento.setFechaInicio(fecha);
        if(evento.getEstado().equals(EstadoSalida.EN_CURSO) || evento.getEstado().equals(EstadoSalida.FINALIZADA)){
            evento.setEstado(EstadoSalida.ABIERTO);
        }

        return new EventoSalidaResponseDto(eventoSalidaRepository.save(evento));
    }

    /**
     * Cambia el nombre de un evento de salida.
     * @param idEventoSalida ID del evento de salida a modificar.
     * @param nombre Nuevo nombre a asignar al evento.
     * @param idResidencia ID de la residencia asociada al evento.
     * @return DTO con los datos del evento actualizado.
     * @throws ResiException si el nombre es nulo, el evento no existe o el nombre ya está en uso en esa residencia.
     */
    public EventoSalidaResponseDto changeNombre(Long idEventoSalida, String nombre, Long idResidencia) {
        if (nombre == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        EventoSalida evento = getEventoSalida(idResidencia,idEventoSalida);

        //El nombre de la salida tiene que ser unico en esa misma residencia
        if (eventoSalidaRepository.existsByNombreAndResidenciaId(nombre, idResidencia)) {
            throw new ResiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }
        evento.setNombre(nombre);
        return new EventoSalidaResponseDto(eventoSalidaRepository.save(evento));

    }

    /**
     * Cambia la descripción de un evento de salida.
     * @param idEventoSalida ID del evento de salida a modificar.
     * @param descripcion Nueva descripción a asignar al evento.
     * @param idResidencia ID de la residencia asociada al evento.
     * @return DTO con los datos del evento actualizado.
     * @throws ResiException si la descripción es nula, el evento no existe o no pertenece a la residencia.
     */
    public EventoSalidaResponseDto changeDescripcion(Long idEventoSalida, String descripcion, Long idResidencia) {
        if (descripcion == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        EventoSalida evento = getEventoSalida(idResidencia,idEventoSalida);

        evento.setDescripcion(descripcion);
        return new EventoSalidaResponseDto(eventoSalidaRepository.save(evento));
    }







    /**
     * Valida que un evento de salida pertenezca a una residencia específica.
     *
     * @param idEventoSalida ID del evento de salida.
     * @param idResidencia ID de la residencia.
     * @return EventoSalida validado.
     * @throws ResiException si el evento no existe o no pertenece a la residencia.
     */
    protected EventoSalida getEventoSalida(Long idResidencia, Long idEventoSalida) {
        if (idEventoSalida == null || idResidencia == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaRepository
                .findById(idEventoSalida)
                .orElseThrow(() -> new ResiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO));;

        // Validar que el evento de salida pertenece a la residencia
        if (!eventoSalida.getResidencia().getId().equals(idResidencia)) {
            throw new ResiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }

        return eventoSalida;
    }

}
