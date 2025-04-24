package com.kevinolarte.resibenissa.services.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.EventoSalidaRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import com.kevinolarte.resibenissa.services.ResidenteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;

@Service
@AllArgsConstructor
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    private final EventoSalidaService eventoSalidaService;
    private final ResidenteService residenteService;


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
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
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

        return new ParticipanteResponseDto(participanteRepository.save(participante));
    }

    public ParticipanteResponseDto updateParticipante(ParticipanteDto input,
                                                      Long idEventoSalida, Long idResidencia, Long idParticipante) {
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
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        // Verificar si el participante existe
        Participante participante = participanteRepository.findById(idParticipante)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO));

        // Verificar si el participante pertenece al evento de salida
        if (!participante.getSalida().getId().equals(idEventoSalida)) {
            throw new ApiException(ApiErrorCode.PARTICIPANTE_INVALIDO);
        }

        if (input.getAsistencia() == null && input.getPreOpinion() == null && input.getPostOpinion() == null) {
            return new ParticipanteResponseDto(participante);
        }


        // Verificar el estado del evento de salida
        if (eventoSalida.getEstado() != EstadoSalida.ABIERTA) {
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        //Verificar si el evento de salida ya ha terminado
        if (eventoSalida.getFechaInicio().isBefore(java.time.LocalDate.now())) {
            if (input.getPostOpinion() != null && !input.getPostOpinion().trim().isEmpty()) {
                participante.setPostOpinion(input.getPostOpinion());
                return new ParticipanteResponseDto(participanteRepository.save(participante));
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
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
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
            throw new ApiException(ApiErrorCode.EVENTO_SALIDA_NO_DISPONIBLE);
        }

        // Eliminar el participante
        participanteRepository.delete(participante);
        return new ParticipanteResponseDto(participante);
    }



}
