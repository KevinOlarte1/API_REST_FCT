package com.kevinolarte.resibenissa.controllers.moduloOrgSalida;


import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.ParticipanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
@RequestMapping("/resi/evento/{idSalida}/participante")
@RestController
@AllArgsConstructor
public class ParticipanteController {

    private final ParticipanteService participanteService;

    /**
     * Registra un nuevo participante en un evento de salida.
     *
     * @param idSalida ID del evento de salida.
     * @param participanteDto DTO con los datos del participante a registrar.
     * @return {@link ResponseEntity} con el participante creado.
     */
    @PostMapping("/add")
    public ResponseEntity<ParticipanteResponseDto> add(
                                                    @PathVariable Long idSalida,
                                                    @RequestBody ParticipanteDto participanteDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(participanteService.add(participanteDto, idSalida, idResidencia));
    }

    /**
     * Obtiene los datos de un participante específico en un evento de salida.
     *
     * @param idSalida ID del evento de salida.
     * @param idParticipante ID del participante a consultar.
     * @return {@link ResponseEntity} con los datos del participante encontrado.
     */
    @GetMapping("{idParticipante}/get")
    public ResponseEntity<ParticipanteResponseDto> get(
                                                    @PathVariable Long idSalida,
                                                    @PathVariable Long idParticipante) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(participanteService.get(idResidencia, idSalida, idParticipante));
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<ParticipanteResponseDto>> getAllParticipantes(
            @PathVariable Long idSalida,
            @RequestParam(required = false) Boolean asistencia,
            @RequestParam(required = false) Long idResidente){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(participanteService.get(idResidencia, idSalida, asistencia, idResidente));

    }

    /**
     * Elimina un participante de un evento de salida.
     *
     * @param idSalida ID del evento de salida.
     * @param idParticipante ID del participante a eliminar.
     */
    @DeleteMapping("/{idParticipante}/delete")
    public ResponseEntity<Void> deleteParticipante(
            @PathVariable Long idSalida,
            @PathVariable Long idParticipante) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        participanteService.deleteParticipante(idResidencia, idSalida, idParticipante);
        return ResponseEntity.noContent().build();
    }


    /**
     * Actualiza los datos de un participante existente en un evento de salida.
     *
     * @param idSalida ID del evento de salida.
     * @param idParticipante ID del participante a actualizar.
     * @param participanteDto DTO con los nuevos datos del participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PatchMapping("/{idParticipante}/update")
    public ResponseEntity<ParticipanteResponseDto> update(
                                                    @PathVariable Long idSalida,
                                                    @PathVariable Long idParticipante,
                                                    @RequestBody ParticipanteDto participanteDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(participanteService.updateParticipante(participanteDto, idResidencia, idSalida, idParticipante));
    }


}
