package com.kevinolarte.resibenissa.controllers.moduloOrgSalida;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.EventoSalidaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/residencia/{idResidencia}/eventoSalida")
@RestController
@AllArgsConstructor
public class EventoSalidaController {

    private final EventoSalidaService eventoSalidaService;


    /**
     * Endpoint para agregar un nuevo evento de salida a una residencia específica.
     *
     * @param idResidencia ID de la residencia a la que se asocia el evento.
     * @param input DTO con los datos del evento de salida.
     * @return EventoSalidaResponseDto con los datos del evento registrado.
     */
    @PostMapping("/add")
    public EventoSalidaResponseDto addEventoSalida(
            @PathVariable Long idResidencia,
            @RequestBody EventoSalidaDto input) {
        return eventoSalidaService.addEventoSalida(input, idResidencia);
    }

    @PatchMapping("/{idEventoSalida}/update")
    public EventoSalidaResponseDto updateEventoSalida(
            @PathVariable  Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestBody EventoSalidaDto input) {
        return eventoSalidaService.updateEventoSalida(input, idEventoSalida, idResidencia);
    }

    @DeleteMapping("/{idEventoSalida}/delete")
    public void deleteEventoSalida(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida) {
        eventoSalidaService.deleteEventoSalida(idEventoSalida, idResidencia);
    }
}
