package com.kevinolarte.resibenissa.services.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import com.kevinolarte.resibenissa.services.ResidenteService;
import com.kevinolarte.resibenissa.specifications.ParticipanteSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Servicio que gestiona las operaciones relacionadas con los participantes de eventos de salida.
 * <p>
 * Permite registrar, actualizar, eliminar, consultar y listar participantes
 * asociados a eventos de salida en una residencia.
 * </p>
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    private final EventoSalidaService eventoSalidaService;
    private final ResidenteService residenteService;


    /**
     * Registra un nuevo participante en un evento de salida.
     *
     * @param input DTO con los datos del participante.
     * @param idEventoSalida ID del evento de salida.
     * @param idResidencia ID de la residencia asociada.
     * @return DTO del participante creado.
     * @throws ApiException si falta algún campo obligatorio, el evento o el residente son inválidos,
     *                      el evento no pertenece a la residencia, el evento está cerrado o ya ha finalizado,
     *                      o el residente ya participa en otra salida el mismo día.
     */
    public ParticipanteResponseDto add(ParticipanteDto input, Long idEventoSalida, Long idResidencia) {
        if (idEventoSalida == null || input.getIdResidente() == null || input.getRecursosHumanos() == null || input.getRecursosMateriales() == null || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.getEventoSalida(idResidencia, idEventoSalida);

        // Verificar si el residente existe
        Residente residente = residenteService.getResidente(idResidencia, input.getIdResidente());


        // Verificar el estado del evento de salida
        if (eventoSalida.getEstado() != EstadoSalida.ABIERTO) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        //Verificamos si ya esta inscrito
        if (participanteRepository.isResidenteInscritoEnEvento(residente.getId(), idEventoSalida))
            throw new ApiException(ApiErrorCode.PARTICIPANTE_YA_REGISTRADO);

        //Verificar si el participante participa en una salida ese mismo dia
        if (participanteRepository.existsByResidenteInOtherEventoSameDay(input.getIdResidente(), idEventoSalida))
            throw new ApiException(ApiErrorCode.PARTICIPANTE_YA_REGISTRADO);



        // Crear el participante
        Participante participante = new Participante();
        participante.setEvento(eventoSalida);
        participante.setResidente(residente);
        participante.setRecursosHumanos(input.getRecursosHumanos());
        participante.setRecursosMateriales(input.getRecursosMateriales());
        if (input.getPreOpinion() != null) {
            participante.setPreOpinion(input.getPreOpinion());
        }

        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }




    /**
     * Obtiene los datos de un participante específico.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante.
     * @return DTO del participante encontrado.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      o no pertenecen a la residencia o evento.
     */
    public ParticipanteResponseDto get(Long idResidencia, Long idEvento, Long idParticipante) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);

        // Devolver el participante
        return new ParticipanteResponseDto(participante);

    }

    /**
     * Obtiene una lista de participantes filtrados por varios parámetros.
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idResidente ID del residente (opcional).
     * @param rM Indica si se filtran por recursos materiales. (opcional)
     * @param rH Indica si se filtran por recursos humanos. (opcional)
     * @param minEdad Edad mínima del residente (opcional).
     * @param maxEdad Edad máxima del residente (opcional).
     * @param preOpinion Indica si se filtran por preopinión. (opcional)
     * @param postOpinion Indica si se filtran por postopinión. (opcional)
     * @param asistenciPermitida Indica si se filtran por asistencia permitida. (opcional)
     * @return Lista de participantes filtrados.
     * @throws ApiException si falta algún campo obligatorio, el evento no existe o no pertenece a la residencia.
     */
    public List<ParticipanteResponseDto> getAll(Long idResidencia, Long idEvento, Long idResidente,  Boolean rM,Boolean rH, Integer minEdad, Integer maxEdad, Boolean preOpinion, Boolean postOpinion, Boolean asistenciPermitida) {
        if (idResidencia == null || idEvento == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.getEventoSalida(idResidencia, idEvento);


        List<Participante> list = participanteRepository.findAll(
                ParticipanteSpecification.withFilters(idResidencia, idEvento, idResidente, rH, rM, minEdad, maxEdad, preOpinion, postOpinion, asistenciPermitida)
        );


        return list.stream()
                .map(ParticipanteResponseDto::new).toList();


    }




    /**
     * Elimina un participante de un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante a eliminar.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      el evento no pertenece a la residencia o ya ha finalizado.
     */
    public void deleteParticipante(Long idResidencia, Long idEvento, Long idParticipante) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);

        EventoSalida eventoSalida = eventoSalidaService.getEventoSalida(idResidencia, idEvento);

        // Verificar si la fecha de inicio del evento de salida ya ha pasado
        if (eventoSalida.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_INMUTABLE);
        }

        // Eliminar el participante
        participanteRepository.delete(participante);
    }




    /**
     * Actualiza los datos de un participante en un evento de salida.
     * @param input DTO con los nuevos datos del participante.
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante a actualizar.
     * @return DTO del participante actualizado.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     */
    public ParticipanteResponseDto update(ParticipanteDto input, Long idResidencia, Long idEvento, Long idParticipante) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);
        if (input == null)
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);

        // Verificar el estado del evento de salida
        if (participante.getEvento().getEstado() != EstadoSalida.ABIERTO &&
                participante.getEvento().getEstado() != EstadoSalida.CERRADO) {
            if (input.getPostOpinion() != null && !input.getPostOpinion().trim().isEmpty()) {
                if (participante.getEvento().getEstado() == EstadoSalida.FINALIZADA) {
                    participante.setPostOpinion(input.getPostOpinion());
                }

            }
        }
        else{
            // Actualizar los campos del participante
            if (input.getRecursosHumanos() != null) {
                participante.setRecursosHumanos(input.getRecursosHumanos());
            }
            if (input.getRecursosMateriales() != null) {
                participante.setRecursosMateriales(input.getRecursosMateriales());
            }
            if (input.getPreOpinion() != null && !input.getPreOpinion().trim().isEmpty()) {
                participante.setPreOpinion(input.getPreOpinion());
            }
        }
        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    /**
     * Permite la asistencia de un participante a un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante.
     * @return DTO del participante con la asistencia permitida.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      o el evento no está abierto.
     */
    public ParticipanteResponseDto allow(Long idResidencia, Long idEvento, Long idParticipante) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);

        // Verificar el estado del evento de salida
        if (participante.getEvento().getEstado() != EstadoSalida.ABIERTO) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }
        participante.setAsistenciaPermitida(true);
        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    /**
     * Deniega la asistencia de un participante a un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante.
     * @return DTO del participante con la asistencia denegada.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      o el evento no está abierto.
     */
    public ParticipanteResponseDto deny(Long idResidencia, Long idEvento, Long idParticipante) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);

        // Verificar el estado del evento de salida
        if (participante.getEvento().getEstado() != EstadoSalida.ABIERTO) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }
        participante.setAsistenciaPermitida(false);
        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    /**
     * Añade una preopinión a un participante en un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante.
     * @param preOpinion Texto de la preopinión.
     * @return DTO del participante con la preopinión añadida.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      o el evento no está abierto.
     */
    public ParticipanteResponseDto addPreOpinion(Long idResidencia, Long idEvento, Long idParticipante, String preOpinion) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);

        // Verificar el estado del evento de salida
        if (participante.getEvento().getEstado() != EstadoSalida.ABIERTO) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }
        // Verificar que la preOpinion no sea nula o vacía
        if (preOpinion != null && !preOpinion.trim().isEmpty()) {
            participante.setPreOpinion(preOpinion);
        }
        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    /**
     * Añade una postopinión a un participante en un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante.
     * @param postOpinion Texto de la postopinión.
     * @return DTO del participante con la postopinión añadida.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      o el evento no está finalizado.
     */
    public ParticipanteResponseDto addPostOpinion(Long idResidencia, Long idEvento, Long idParticipante, String postOpinion) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);

        // Verificamos que la el estado no sea finalozado.
        if (participante.getEvento().getEstado() != EstadoSalida.FINALIZADA) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }
        // Verificar que la postOpinion no sea nula o vacía
        if (postOpinion != null && !postOpinion.trim().isEmpty()) {
            participante.setPostOpinion(postOpinion);
        }
        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    /**
     * Cambia los recursos de un participante en un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante.
     * @param rH Indica si se asignan recursos humanos (opcional).
     * @param rM Indica si se asignan recursos materiales (opcional).
     * @return DTO del participante con los recursos actualizados.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      o el evento no está abierto o cerrado.
     */
    public ParticipanteResponseDto changeRecursos(Long idResidencia, Long idEvento, Long idParticipante, Boolean rH, Boolean rM) {
        Participante participante = getParticipante(idResidencia, idEvento, idParticipante);

        // Verificar el estado del evento de salida (si no es abierto o cerrado)
        if (participante.getEvento().getEstado() != EstadoSalida.ABIERTO &&
            participante.getEvento().getEstado() != EstadoSalida.CERRADO) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }
        // Verificar que la preOpinion no sea nula o vacía
        if (rH != null) {
            participante.setRecursosHumanos(rH);
        }
        if (rM != null) {
            participante.setRecursosMateriales(rM);
        }
        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }




    /**
     * Obtiene un participante específico y valida que pertenezca a un evento de salida en una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante.
     * @return El participante encontrado.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      o no pertenecen a la residencia o evento.
     */
    public Participante getParticipante(Long idResidencia, Long idEvento, Long idParticipante){
        if (idResidencia == null || idEvento == null || idParticipante == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.getEventoSalida(idResidencia, idEvento);
        if (eventoSalida == null) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }
        // Verificar si el evento de salida pertenece a la residencia
        if (!eventoSalida.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }


        // Verificar si el participante existe
        Participante participante = participanteRepository.findById(idParticipante)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO));

        // Verificar si el participante pertenece al evento de salida
        if (!participante.getEvento().getId().equals(idEvento)) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO);
        }
        return participante;
    }

}
