package com.kevinolarte.resibenissa.controllers.moduloOrgSalida.participante;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.ParticipanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los participantes de un evento de salida en una residencia.
 * <p>
 * Permite agregar, consultar, actualizar, eliminar y listar participantes asociados
 * a un evento específico dentro de una residencia.
 * </p>
 *
 * URL Base: {@code /admin/resi/{idResidencia}/evento/{idEvento}/participante}
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi/{idResidencia}/evento/{idEvento}/participante")
@RestController
@AllArgsConstructor
public class ParticipanteAdminController {

    private final ParticipanteService participanteService;

    /**
     * Registra un nuevo participante en un evento de salida.
     *
     * @param idResidencia ID de la residencia donde se registra el participante.
     * @param idEvento ID del evento de salida al que se registra el participante.
     * @param participanteDto DTO con los datos del participante a registrar.
     * @return {@link ResponseEntity} con el participante creado.
     */
    @PostMapping("/add")
    public ResponseEntity<ParticipanteResponseDto> add(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @RequestBody ParticipanteDto participanteDto) {

        return ResponseEntity.ok(participanteService.add(participanteDto, idEvento, idResidencia));
    }

    /**
     * Obtiene los datos de un participante específico en un evento de salida.
     *
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida al que pertenece el participante.
     * @param idParticipante ID del participante a consultar.
     * @return {@link ResponseEntity} con los datos del participante encontrado.
     */
    @GetMapping("{idParticipante}/get")
    public ResponseEntity<ParticipanteResponseDto> get(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante) {

        return ResponseEntity.ok(participanteService.get(idResidencia, idEvento, idParticipante));
    }


    /**
     * Obtiene una lista de todos los participantes de un evento de salida, con opciones de filtrado.
     *
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida del que se obtienen los participantes.
     * @param idResidente ID del residente (opcional) para filtrar por residente específico.
     * @param rM Recurso material (opcional) para filtrar por recurso material.
     * @param rH Recurso humano (opcional) para filtrar por recurso humano.
     * @param minEdad Edad mínima del participante (opcional).
     * @param maxEdad Edad máxima del participante (opcional).
     * @param preOpinion Filtra por participantes con opinión previa (opcional).
     * @param postOpinion Filtra por participantes con opinión posterior (opcional).
     * @param asistenciPermitida Filtra por participantes con asistencia permitida (opcional).
     * @return {@link ResponseEntity} con la lista de participantes encontrados.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ParticipanteResponseDto>> getAllParticipantes(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @RequestParam(required = false) Long idResidente,
                                @RequestParam(required = false) Boolean rM,
                                @RequestParam(required = false) Boolean rH,
                                @RequestParam(required = false)Integer minEdad,
                                @RequestParam(required = false)Integer maxEdad,
                                @RequestParam(required = false)Boolean preOpinion,
                                @RequestParam(required = false)Boolean postOpinion,
                                @RequestParam(required = false)Boolean asistenciPermitida) {

        List<ParticipanteResponseDto> e =participanteService.getAll(idResidencia, idEvento, idResidente, rM, rH, minEdad, maxEdad, preOpinion, postOpinion, asistenciPermitida);
        return ResponseEntity.ok(e);

    }


    /**
     * Elimina un participante de un evento de salida.
     *
     * @param idParticipante ID del participante a eliminar.
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida del que se elimina el participante.
     * @return {@link ResponseEntity} con el estado de la operación.
     */
    @DeleteMapping("/{idParticipante}/delete")
    public ResponseEntity<Void> deleteParticipante(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante) {

        participanteService.deleteParticipante(idResidencia, idEvento, idParticipante);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza los datos de un participante existente en un evento de salida.
     *
     * @param idParticipante ID del participante a actualizar.
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida al que pertenece el participante.
     * @param participanteDto DTO con los nuevos datos del participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PatchMapping("/{idParticipante}/update")
    public ResponseEntity<ParticipanteResponseDto> update(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante,
                                @RequestBody ParticipanteDto participanteDto) {

        return ResponseEntity.ok(participanteService.update(participanteDto, idResidencia, idEvento, idParticipante));
    }


    /**
     * Añade una opinión previa de un participante en un evento de salida.
     *
     * @param idParticipante ID del participante al que se le añade la opinión.
     * @param preOpinion Opinión previa del participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PatchMapping("/{idParticipante}/addPreOpinion")
    public ResponseEntity<ParticipanteResponseDto> addPreOpinion(
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante,
                                @RequestParam String preOpinion) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(participanteService.addPreOpinion(idResidencia, idEvento, idParticipante, preOpinion));
    }

    /**
     * Agrega una opinión posterior a la participación de un residente en un evento de salida.
     *
     * @param idParticipante ID del participante al que se le agregará la opinión.
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida al que pertenece el participante.
     * @param postOpinion Opinión posterior a la participación.
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PatchMapping("/{idParticipante}/addPostOpinion")
    public ResponseEntity<ParticipanteResponseDto> addPostOpinion(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante,
                                @RequestParam String postOpinion) {


        return ResponseEntity.ok(participanteService.addPostOpinion(idResidencia, idEvento, idParticipante, postOpinion));
    }

    /**
     * Cambia los recursos asignados a un participante en un evento de salida.
     *
     * @param idParticipante ID del participante a actualizar.
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida al que pertenece el participante.
     * @param rH Recurso humano (opcional).
     * @param rM Recurso material (opcional).
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PatchMapping("/{idParticipante}/changeRecursos")
    public ResponseEntity<ParticipanteResponseDto> changeRecursos(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante,
                                @RequestParam (required = false) Boolean rH,
                                @RequestParam (required = false) Boolean rM){

        return ResponseEntity.ok(participanteService.changeRecursos(idResidencia, idEvento, idParticipante, rH, rM));
    }


    /**
     * Acepta la participación un participante en un evento de salida.
     *
     * @param idParticipante ID del participante a aceptar.
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida al que pertenece el participante.
     * @return {@link ResponseEntity} con el participante aceptado.
     */
    @PostMapping("/{idParticipante}/allow")
    public ResponseEntity<ParticipanteResponseDto> allow(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante) {

        return ResponseEntity.ok(participanteService.allow(idResidencia, idEvento, idParticipante));
    }

    /**
     * Deniega la participación de un residente en un evento de salida.
     *
     * @param idParticipante ID del participante a denegar.
     * @param idResidencia ID de la residencia donde se encuentra el evento.
     * @param idEvento ID del evento de salida al que pertenece el participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PostMapping("/{idParticipante}/deny")
    public ResponseEntity<ParticipanteResponseDto> deny(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idEvento,
                                @PathVariable Long idParticipante) {

        return ResponseEntity.ok(participanteService.deny(idResidencia, idEvento, idParticipante));
    }

}
