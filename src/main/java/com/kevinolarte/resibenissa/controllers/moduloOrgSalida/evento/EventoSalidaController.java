package com.kevinolarte.resibenissa.controllers.moduloOrgSalida.evento;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.EventoSalidaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestionar los eventos de salida de una residencia.
 * <p>
 * Permite crear, actualizar, eliminar, consultar y listar eventos de salida
 * asociados a una residencia específica.
 * </p>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/evento")
@RestController
@AllArgsConstructor
public class EventoSalidaController {

    private final EventoSalidaService eventoSalidaService;


    /**
     * Crea un nuevo evento de salida en una residencia.
     *
     * @param input DTO que contiene los datos del evento de salida a crear.
     * @return {@link ResponseEntity} con el evento de salida creado.
     */
    @PostMapping("/add")
    public ResponseEntity<EventoSalidaResponseDto> add(
                                                    @RequestBody EventoSalidaDto input) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalidaService.add(input, idResidencia));
    }


    /**
     * Obtiene los datos de un evento de salida específico.
     *
     * @param idEventoSalida ID del evento de salida a consultar.
     * @return {@link ResponseEntity} con los datos del evento de salida encontrado.
     */
    @GetMapping("/{idEventoSalida}/get")
    public ResponseEntity<EventoSalidaResponseDto> getEventoSalida(
                                                    @PathVariable Long idEventoSalida) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(eventoSalidaService.get(idEventoSalida, idResidencia));
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<EventoSalidaResponseDto>> getAllEventosSalida(
                                                        @RequestParam(required = false ) LocalDate fecha,
                                                        @RequestParam(required = false) LocalDate minFecha,
                                                        @RequestParam(required = false) LocalDate maxFecha,
                                                        @RequestParam(required = false) EstadoSalida estado,
                                                        @RequestParam(required = false) Long idResidente,
                                                        @RequestParam(required = false) Long idParticipante,
                                                        @RequestParam(required = false) Integer minRH,
                                                        @RequestParam(required = false) Integer maxRH,
                                                        @RequestParam(required = false) Integer minRM,
                                                        @RequestParam(required = false) Integer maxRM) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(eventoSalidaService.getAll(idResidencia, fecha, minFecha, maxFecha, estado, idResidente, idParticipante, minRH, maxRH, minRM, maxRM));
    }



    /**
     * Elimina un evento de salida de una residencia.
     * <p>
     * La eliminación incluye también a todos los participantes asociados al evento.
     * </p>
     *
     * @param idEventoSalida ID del evento de salida a eliminar.
     */
    @DeleteMapping("/{idEventoSalida}/delete")
    public ResponseEntity<Void> delete(
                                @PathVariable Long idEventoSalida) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        eventoSalidaService.delete(idEventoSalida, idResidencia);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Cambia el nombre de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param nombre Nuevo nombre del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     */
    @PatchMapping("/{idEventoSalida}/changeNombre")
    public ResponseEntity<EventoSalidaResponseDto> changeNombre(
            @PathVariable Long idEventoSalida,
            @RequestParam String nombre) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(eventoSalidaService.changeNombre(idEventoSalida, nombre, idResidencia));
    }

    /**
     * Cambia la descripción de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param descripcion Nueva descripción del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     */
    @PatchMapping("/{idEventoSalida}/changeDescripcion")
    public ResponseEntity<EventoSalidaResponseDto> changeDescripcion(
            @PathVariable Long idEventoSalida,
            @RequestParam String descripcion) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(eventoSalidaService.changeDescripcion(idEventoSalida, descripcion, idResidencia));
    }

    /**
     * Cambia la fecha de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param fecha Nueva fecha del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     */
    @PatchMapping("/{idEventoSalida}/ChangeFecha")
    public ResponseEntity<EventoSalidaResponseDto> changeFecha(
            @PathVariable Long idEventoSalida,
            @RequestParam LocalDateTime fecha) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(eventoSalidaService.changeFecha(idEventoSalida, fecha, idResidencia));
    }

    /**
     * Cambia el estado de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param estado Nuevo estado del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     */
    @PatchMapping("/{idEventoSalida}/changeEstado")
    public ResponseEntity<EventoSalidaResponseDto> changeEstado(
            @PathVariable Long idEventoSalida,
            @RequestParam EstadoSalida estado) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idResidencia = ((User) auth.getPrincipal()).getResidencia().getId();
        return ResponseEntity.ok(eventoSalidaService.changeEstado(idEventoSalida, estado, idResidencia));
    }




}
