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
     * Obtiene una lista de participantes de un evento de salida, con filtros opcionales.
     *
     * @param idResidente ID del residente (opcional).
     * @return {@link ResponseEntity} con la lista de participantes.
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
     * @param participanteDto DTO con los nuevos datos del participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PatchMapping("/{idParticipante}/update")
    public ResponseEntity<ParticipanteResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idEvento,
            @PathVariable Long idParticipante,
            @RequestBody ParticipanteDto participanteDto) {

        return ResponseEntity.ok(participanteService.updateParticipante(participanteDto, idResidencia, idEvento, idParticipante));
    }
}
