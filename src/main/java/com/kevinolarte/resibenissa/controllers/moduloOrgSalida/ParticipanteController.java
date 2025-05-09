package com.kevinolarte.resibenissa.controllers.moduloOrgSalida;


import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.ParticipanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/resi/{idResidencia}/evento/{idSalida}/participante")
@RestController
@AllArgsConstructor
public class ParticipanteController {

    private final ParticipanteService participanteService;

    /**
     * Registra un nuevo participante en un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idSalida ID del evento de salida.
     * @param participanteDto DTO con los datos del participante a registrar.
     * @return {@link ResponseEntity} con el participante creado.
     */
    @PostMapping("/add")
    public ResponseEntity<ParticipanteResponseDto> add(
                                                    @PathVariable Long idResidencia,
                                                    @PathVariable Long idSalida,
                                                    @RequestBody ParticipanteDto participanteDto) {

        return ResponseEntity.ok(participanteService.add(participanteDto, idSalida, idResidencia));
    }

    /**
     * Obtiene los datos de un participante específico en un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idSalida ID del evento de salida.
     * @param idParticipante ID del participante a consultar.
     * @return {@link ResponseEntity} con los datos del participante encontrado.
     */
    @GetMapping("{idParticipante}/get")
    public ResponseEntity<ParticipanteResponseDto> getParticipante(
                                                    @PathVariable Long idResidencia,
                                                    @PathVariable Long idSalida,
                                                    @PathVariable Long idParticipante) {
        return ResponseEntity.ok(participanteService.getParticipante(idResidencia, idSalida, idParticipante));
    }

    /**
     * Lista todos los participantes de un evento de salida, aplicando filtros opcionales.
     *
     * @param idResidencia ID de la residencia.
     * @param idSalida ID del evento de salida.
     * @param participanteDto (opcional) Filtros de búsqueda para los participantes (ej: asistencia).
     * @return {@link ResponseEntity} con la lista de participantes encontrados.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ParticipanteResponseDto>> getAllParticipantes(
                                                        @PathVariable Long idResidencia,
                                                        @PathVariable Long idSalida,
                                                        @RequestBody(required = false) ParticipanteDto participanteDto) {
        return ResponseEntity.ok(participanteService.getParticiapnte(idResidencia, idSalida, participanteDto));

    }

    /**
     * Actualiza los datos de un participante existente en un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idSalida ID del evento de salida.
     * @param idParticipante ID del participante a actualizar.
     * @param participanteDto DTO con los nuevos datos del participante.
     * @return {@link ResponseEntity} con el participante actualizado.
     */
    @PatchMapping("/{idParticipante}/update")
    public ResponseEntity<ParticipanteResponseDto> updateParticipante(
                                                    @PathVariable Long idResidencia,
                                                    @PathVariable Long idSalida,
                                                    @PathVariable Long idParticipante,
                                                    @RequestBody ParticipanteDto participanteDto) {
        return ResponseEntity.ok(participanteService.updateParticipante(participanteDto, idResidencia, idSalida, idParticipante));
    }

    /**
     * Elimina un participante de un evento de salida.
     *
     * @param idResidencia ID de la residencia.
     * @param idSalida ID del evento de salida.
     * @param idParticipante ID del participante a eliminar.
     */
    @DeleteMapping("/{idParticipante}/delete")
    public void deleteParticipante(
                                    @PathVariable Long idResidencia,
                                    @PathVariable Long idSalida,
                                    @PathVariable Long idParticipante) {
        participanteService.deleteParticipante(idResidencia, idSalida, idParticipante);
    }
}
