package com.kevinolarte.resibenissa.controllers.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.EventoSalidaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
@RequestMapping("/resi/{idResidencia}/evento")
@RestController
@AllArgsConstructor
public class EventoSalidaController {

    private final EventoSalidaService eventoSalidaService;


    /**
     * Crea un nuevo evento de salida en una residencia.
     *
     * @param idResidencia ID de la residencia a la que se asocia el evento.
     * @param input DTO que contiene los datos del evento de salida a crear.
     * @return {@link ResponseEntity} con el evento de salida creado.
     */
    @PostMapping("/add")
    public ResponseEntity<EventoSalidaResponseDto> add(
            @PathVariable Long idResidencia,
            @RequestBody EventoSalidaDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalidaService.add(input, idResidencia));
    }


    /**
     * Obtiene los datos de un evento de salida específico.
     *
     * @param idResidencia ID de la residencia asociada.
     * @param idEventoSalida ID del evento de salida a consultar.
     * @return {@link ResponseEntity} con los datos del evento de salida encontrado.
     */
    @GetMapping("/{idEventoSalida}/get")
    public ResponseEntity<EventoSalidaResponseDto> getEventoSalida(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida) {

        return ResponseEntity.ok(eventoSalidaService.get(idEventoSalida, idResidencia));
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<EventoSalidaResponseDto>> getAllEventosSalida(
            @PathVariable Long idResidencia,
            @RequestParam(required = false ) LocalDate fecha,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false)EstadoSalida estado,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Long idParticipante) {
        return ResponseEntity.ok(eventoSalidaService.getAll(idResidencia, fecha, year, month, estado, idResidente, idParticipante));
    }



    /**
     * Elimina un evento de salida de una residencia.
     * <p>
     * La eliminación incluye también a todos los participantes asociados al evento.
     * </p>
     *
     * @param idResidencia ID de la residencia asociada.
     * @param idEventoSalida ID del evento de salida a eliminar.
     */
    @DeleteMapping("/{idEventoSalida}/delete")
    public void delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida) {
        eventoSalidaService.delete(idEventoSalida, idResidencia);
    }

    /**
     * Actualiza los datos de un evento de salida existente.
     * <p>
     * Solo es posible actualizar la fecha y el estado del evento.
     * </p>
     *
     * @param idResidencia ID de la residencia asociada al evento.
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param input DTO que contiene los nuevos datos del evento.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     */
    @PatchMapping("/{idEventoSalida}/update")
    public ResponseEntity<EventoSalidaResponseDto> updateEventoSalida(
            @PathVariable  Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestBody EventoSalidaDto input) {
        return ResponseEntity.ok(eventoSalidaService.update(input, idEventoSalida, idResidencia));
    }




}
