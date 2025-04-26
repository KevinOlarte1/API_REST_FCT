package com.kevinolarte.resibenissa.controllers.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.EventoSalidaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los eventos de salida de una residencia.
 * <p>
 * Permite crear, actualizar, eliminar, obtener y listar eventos asociados a una residencia específica.
 * </p>
 * @author Kevin Olarte
 */
@RequestMapping("/resi/{idResidencia}/evento")
@RestController
@AllArgsConstructor
public class EventoSalidaController {

    private final EventoSalidaService eventoSalidaService;


    /**
     * Crea un nuevo evento de salida para una residencia específica.
     *
     * @param idResidencia ID de la residencia a la que se asocia el evento.
     * @param input DTO que contiene los datos del evento de salida.
     * @return {@link ResponseEntity} que contiene el evento de salida creado.
     */
    @PostMapping("/add")
    public ResponseEntity<EventoSalidaResponseDto> addEventoSalida(
            @PathVariable Long idResidencia,
            @RequestBody EventoSalidaDto input) {
        return ResponseEntity.ok(eventoSalidaService.addEventoSalida(input, idResidencia));
    }

    /**
     * Actualiza un evento de salida existente.
     * <p>
     *     Se actualizara unicamente la fecha y el estado.
     *  </p>
     *
     * @param idResidencia ID de la residencia asociada al evento.
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param input DTO que contiene los nuevos datos del evento.
     * @return {@link ResponseEntity} con el evento actualizado.
     */
    @PatchMapping("/{idEventoSalida}/update")
    public ResponseEntity<EventoSalidaResponseDto> updateEventoSalida(
            @PathVariable  Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestBody EventoSalidaDto input) {
        return ResponseEntity.ok(eventoSalidaService.updateEventoSalida(input, idEventoSalida, idResidencia));
    }

    /**
     * Elimina un evento de salida de una residencia. Eso incluye a sus particicpantes
     *
     * @param idResidencia ID de la residencia asociada.
     * @param idEventoSalida ID del evento de salida a eliminar.
     */
    @DeleteMapping("/{idEventoSalida}/delete")
    public void deleteEventoSalida(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida) {
        eventoSalidaService.deleteEventoSalida(idEventoSalida, idResidencia);
    }


    /**
     * Obtiene los datos de un evento de salida específico.
     *
     * @param idResidencia ID de la residencia asociada.
     * @param idEventoSalida ID del evento de salida a consultar.
     * @return {@link ResponseEntity} que contiene los datos del evento de salida.
     */
    @GetMapping("/{idEventoSalida}/get")
    public ResponseEntity<EventoSalidaResponseDto> getEventoSalida(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida) {

        return ResponseEntity.ok(eventoSalidaService.getEventoSalida(idEventoSalida, idResidencia));
    }

    /**
     * Lista todos los eventos de salida de una residencia, opcionalmente aplicando filtros.
     *
     * @param idResidencia ID de la residencia de la que se quieren obtener los eventos.
     * @param input (Opcional) Filtros de búsqueda para los eventos.
     * @return {@link ResponseEntity} que contiene la lista de eventos de salida encontrados.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<EventoSalidaResponseDto>> getAllEventosSalida(
            @PathVariable Long idResidencia,
            @RequestBody(required = false) EventoSalidaDto input) {
        return ResponseEntity.ok(eventoSalidaService.getEventoSalida(idResidencia, input));
    }

}
