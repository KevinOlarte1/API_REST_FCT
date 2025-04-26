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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
    public ParticipanteResponseDto addParticipante(ParticipanteDto input,
                                                   Long idEventoSalida, Long idResidencia) {
        if (idEventoSalida == null || input.getIdResidente() == null || input.getAsistencia() == null || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.findById(idEventoSalida);
        if (eventoSalida == null) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }

        // Verificar si el evento de salida pertenece a la residencia
        if (!eventoSalida.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }

        // Verificar si el residente existe
        Residente residente = residenteService.findById(input.getIdResidente());
        if (residente == null) {
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }

        // Verificar si el residente pertenece a la residencia
        if (!residente.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }

        // Verificar si el evento de salida ya ha terminado
        if (eventoSalida.getFechaInicio().isBefore(java.time.LocalDate.now())) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        // Verificar el estado del evento de salida
        if (eventoSalida.getEstado() != EstadoSalida.ABIERTA) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        //Verificar si el participante participa en una salida ese mismo dia
        if (participanteRepository.existsByResidenteInOtherSalidaSameDay(input.getIdResidente(), idEventoSalida)) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_YA_REGISTRADO);
        }


        // Crear el participante
        Participante participante = new Participante();
        participante.setSalida(eventoSalida);
        participante.setResidente(residente);
        participante.setAyuda(input.getAsistencia());
        if (input.getPreOpinion() != null) {
            participante.setPreOpinion(input.getPreOpinion());
        }

        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    /**
     * Actualiza los datos de un participante existente.
     *
     * @param input DTO con los nuevos datos.
     * @param idResidencia ID de la residencia.
     * @param idEventoSalida ID del evento de salida.
     * @param idParticipante ID del participante.
     * @return DTO del participante actualizado.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      el evento no pertenece a la residencia, o el evento está cerrado.
     */
    public ParticipanteResponseDto updateParticipante(ParticipanteDto input,
                                                      Long idResidencia, Long idEventoSalida, Long idParticipante) {
        if (idEventoSalida == null || idResidencia == null || idParticipante == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.findById(idEventoSalida);
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
        if (!participante.getSalida().getId().equals(idEventoSalida)) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO);
        }

        // Verificar el estado del evento de salida
        if (eventoSalida.getEstado() != EstadoSalida.ABIERTA) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        // Si no hay nada que cambiar lo devolvemos como es.
        if (input == null || (input.getAsistencia() == null && input.getPreOpinion() == null && input.getPostOpinion() == null)) {
            return new ParticipanteResponseDto(participante);
        }

        //Verificar si el evento de salida ya ha terminado
        if (eventoSalida.getFechaInicio().isBefore(java.time.LocalDate.now())) {
            if (input.getPostOpinion() != null && !input.getPostOpinion().trim().isEmpty()) {
                participante.setPostOpinion(input.getPostOpinion());
            }
        }
        else {
            // Verificar que la preOpinion no sea nula o vacía
            if (input.getPreOpinion() != null && !input.getPreOpinion().trim().isEmpty()) {
                participante.setPreOpinion(input.getPreOpinion());
            }

            // Verificar que la asistencia no sea nula
            if (input.getAsistencia() != null){
                participante.setAyuda(input.getAsistencia());
            }
        }
        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    /**
     * Elimina un participante de un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param idParticipante ID del participante a eliminar.
     * @return DTO del participante eliminado.
     * @throws ApiException si falta algún campo obligatorio, el evento o el participante son inválidos,
     *                      el evento no pertenece a la residencia o ya ha finalizado.
     */
    public ParticipanteResponseDto deleteParticipante(Long idResidencia, Long idEvento, Long idParticipante) {
        if (idResidencia == null || idEvento == null || idParticipante == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.findById(idEvento);
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
        if (!participante.getSalida().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO);
        }

        // Verificar si la fecha de inicio del evento de salida ya ha pasado
        if (eventoSalida.getFechaInicio().isBefore(java.time.LocalDate.now())) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_INMUTABLE);
        }

        // Eliminar el participante
        participanteRepository.delete(participante);
        return new ParticipanteResponseDto(participante);
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
    public ParticipanteResponseDto getParticipante(Long idResidencia, Long idEvento, Long idParticipante) {
        if (idResidencia == null || idEvento == null || idParticipante == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.findById(idEvento);
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
        if (!participante.getSalida().getId().equals(idEvento)) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO);
        }

        // Devolver el participante
        return new ParticipanteResponseDto(participante);

    }

    /**
     * Lista todos los participantes de un evento de salida, aplicando filtros opcionales.
     *
     * @param idResdencia ID de la residencia.
     * @param idEvento ID del evento de salida.
     * @param input DTO con filtros de búsqueda (opcional).
     * @return Lista de participantes encontrados.
     * @throws ApiException si falta algún campo obligatorio o el evento es inválido.
     */
    public List<ParticipanteResponseDto> getParticiapnte(Long idResdencia, Long idEvento, ParticipanteDto input){
        if (idResdencia == null || idEvento == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Verificar si el evento de salida existe
        EventoSalida eventoSalida = eventoSalidaService.findById(idEvento);
        if (eventoSalida == null) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }

        // Verificar si el evento de salida pertenece a la residencia
        if (!eventoSalida.getResidencia().getId().equals(idResdencia)) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_INVALIDO);
        }

        //Verificar si tiene el filtrado de asistencia o no.
        if (input != null && input.getAsistencia() != null)
            return participanteRepository.findByAyudaAndSalida(input.getAsistencia(), eventoSalida)
                    .stream()
                    .map(ParticipanteResponseDto::new).toList();
        else
            return participanteRepository.findBySalida(eventoSalida)
                    .stream()
                    .map(ParticipanteResponseDto::new).toList();


    }



}
