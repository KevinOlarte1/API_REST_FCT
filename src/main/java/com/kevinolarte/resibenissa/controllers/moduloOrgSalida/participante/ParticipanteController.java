package com.kevinolarte.resibenissa.controllers.moduloOrgSalida.participante;


import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.EmailService;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.ParticipanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
 * URL base: /resi/evento/{idEvento}/participante
 * @author Kevin Olarte
 */
@RequestMapping("/resi/evento/{idEvento}/participante")
@RestController
@AllArgsConstructor
public class ParticipanteController {

    private final ParticipanteService participanteService;
    private final EmailService emailService;

    /**
     * Registra un nuevo participante en un evento de salida.
     *
     * @param participanteDto DTO con los datos del participante a registrar.
     * @return {@link ResponseEntity} con el participante creado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/add")
    public ResponseEntity<ParticipanteResponseDto> add(@PathVariable Long idEvento,
                                                       @RequestBody ParticipanteDto participanteDto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            ParticipanteResponseDto dto = participanteService.add(participanteDto, idEvento, currentUser.getResidencia().getId());
            emailService.sendNotificationParticipante(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }


    /**
     * Obtiene los datos de un participante específico en un evento de salida.
     *
     * @param idParticipante ID del participante a consultar.
     * @return {@link ResponseEntity} con los datos del participante encontrado.
     */
    @GetMapping("{idParticipante}/get")
    public ResponseEntity<ParticipanteResponseDto> get(@PathVariable Long idEvento,
                                                       @PathVariable Long idParticipante) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {

            return ResponseEntity.ok(participanteService.get(currentUser.getResidencia().getId(), idEvento, idParticipante));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }


    /**
     * Obtiene todos los participantes de un evento de salida, con filtros opcionales.
     *
     * @param idEvento ID del evento del que se obtienen los participantes.
     * @param idResidente ID del residente (opcional).
     * @param rM Recurso material (opcional).
     * @param rH Recurso humano (opcional).
     * @param minEdad Edad mínima del participante (opcional).
     * @param maxEdad Edad máxima del participante (opcional).
     * @param preOpinion Si se filtran por opinión previa (opcional).
     * @param postOpinion Si se filtran por opinión posterior (opcional).
     * @param asistenciPermitida Si se filtran por asistencia permitida (opcional).
     * @return {@link ResponseEntity} con la lista de participantes filtrados.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ParticipanteResponseDto>> getAllParticipantes(
            @PathVariable Long idEvento,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Boolean rM,
            @RequestParam(required = false) Boolean rH,
            @RequestParam(required = false) Integer minEdad,
            @RequestParam(required = false) Integer maxEdad,
            @RequestParam(required = false) Boolean preOpinion,
            @RequestParam(required = false) Boolean postOpinion,
            @RequestParam(required = false) Boolean asistenciPermitida) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(participanteService.getAll(currentUser.getResidencia().getId(), idEvento, idResidente, rM, rH, minEdad, maxEdad, preOpinion, postOpinion, asistenciPermitida));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Elimina un participante de un evento de salida.
     *
     * @param idParticipante ID del participante a eliminar.
     * @return {@link ResponseEntity} sin contenido si la eliminación fue exitosa.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @DeleteMapping("/{idParticipante}/delete")
    public ResponseEntity<Void> deleteParticipante(@PathVariable Long idEvento,
                                                   @PathVariable Long idParticipante) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            participanteService.deleteParticipante(currentUser.getResidencia().getId(), idEvento, idParticipante);
            return ResponseEntity.noContent().build();
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Añade una opinión previa de un participante en un evento de salida.
     *
     * @param idParticipante ID del participante al que se le añade la opinión.
     * @param preOpinion Opinión previa del participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idParticipante}/addPreOpinion")
    public ResponseEntity<ParticipanteResponseDto> addPreOpinion(@PathVariable Long idEvento,
                                                                 @PathVariable Long idParticipante,
                                                                 @RequestParam String preOpinion) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {

            return ResponseEntity.ok(participanteService.addPreOpinion(currentUser.getResidencia().getId(), idEvento, idParticipante, preOpinion));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Agrega una opinión posterior a la participación de un residente en un evento de salida.
     *
     * @param idParticipante ID del participante al que se le agregará la opinión.
     * @param postOpinion Opinión posterior a la participación.
     * @return {@link ResponseEntity} con el participante actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idParticipante}/addPostOpinion")
    public ResponseEntity<ParticipanteResponseDto> addPostOpinion(@PathVariable Long idEvento,
                                                                  @PathVariable Long idParticipante,
                                                                  @RequestParam String postOpinion) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {

            return ResponseEntity.ok(participanteService.addPostOpinion(currentUser.getResidencia().getId(), idEvento, idParticipante, postOpinion));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Cambia los recursos asignados a un participante en un evento de salida.
     *
     * @param idParticipante ID del participante a actualizar.
     * @param rH Recurso humano (opcional).
     * @param rM Recurso material (opcional).
     * @return {@link ResponseEntity} con el participante actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idParticipante}/changeRecursos")
    public ResponseEntity<ParticipanteResponseDto> changeRecursos(@PathVariable Long idEvento,
                                                                  @PathVariable Long idParticipante,
                                                                  @RequestParam(required = false) Boolean rH,
                                                                  @RequestParam(required = false) Boolean rM) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {

            return ResponseEntity.ok(participanteService.changeRecursos(currentUser.getResidencia().getId(), idEvento, idParticipante, rH, rM));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }


    /**
     * Acepta la participación un participante en un evento de salida.
     *
     * @param idParticipante ID del participante a aceptar.
     * @return {@link ResponseEntity} con el participante aceptado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/{idParticipante}/allow")
    public ResponseEntity<ParticipanteResponseDto> allow(@PathVariable Long idEvento,
                                                         @PathVariable Long idParticipante) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(participanteService.allow(currentUser.getResidencia().getId(), idEvento, idParticipante));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Deniega la participación de un residente en un evento de salida.
     *
     * @param idParticipante ID del participante a denegar.
     * @return {@link ResponseEntity} con el participante actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/{idParticipante}/deny")
    public ResponseEntity<ParticipanteResponseDto> deny(@PathVariable Long idEvento,
                                                        @PathVariable Long idParticipante) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {

            return ResponseEntity.ok(participanteService.deny(currentUser.getResidencia().getId(), idEvento, idParticipante));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Actualiza los datos de un participante existente en un evento de salida.
     *
     * @param idParticipante ID del participante a actualizar.
     * @param participanteDto DTO con los nuevos datos del participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idParticipante}/update")
    public ResponseEntity<ParticipanteResponseDto> update(@PathVariable Long idEvento,
                                                          @PathVariable Long idParticipante,
                                                          @RequestBody ParticipanteDto participanteDto) {
       User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {

            return ResponseEntity.ok(participanteService.update(participanteDto, currentUser.getResidencia().getId(), idEvento, idParticipante));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }




}
