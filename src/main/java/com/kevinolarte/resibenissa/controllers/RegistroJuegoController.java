package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.models.RegistroJuego;
import com.kevinolarte.resibenissa.services.RegistroJuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los registros de juegos jugados por los residentes.
 * <p>
 * Permite agregar nuevos registros, consultar estadísticas con múltiples filtros
 * y eliminar registros específicos.
 * </p>
 * <p>
 * Todas las rutas expuestas están bajo el prefijo <code>/resi/juegos/stats</code>.
 * </p>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/juegos/stats")
@RestController
@AllArgsConstructor
public class RegistroJuegoController {

    private final RegistroJuegoService registroJuegoService;

    /**
     * Agrega un nuevo registro de juego al sistema.
     * <p>
     * Recibe un DTO con los datos de la partida (residente, juego, duración, fallos, etc.)
     * y delega la persistencia al servicio correspondiente.
     * </p>
     *
     * @param registroJuegoDto DTO con los datos del registro de juego.
     * @return {@link ResponseEntity} con el DTO del registro guardado y estado HTTP 200.
     */
    @PostMapping("/add")
    public ResponseEntity<RegistroJuegoResponseDto> addRegistroJuego(@RequestBody RegistroJuegoDto registroJuegoDto) {
        RegistroJuegoResponseDto registroJuego = registroJuegoService.save(registroJuegoDto);
        return ResponseEntity.ok(registroJuego);

    }

    /**
     * Obtiene las estadísticas de juegos jugados aplicando filtros opcionales.
     * <p>
     * Permite filtrar por ID de residente, ID de residencia, ID de juego, y por fecha (año, mes, día).
     * Si no se aplica ningún filtro, se devuelven todos los registros.
     * </p>
     *
     * @param idResidente  ID del residente (opcional).
     * @param idResidencia ID de la residencia (opcional).
     * @param idJuego      ID del juego (opcional).
     * @param year         Año de ejecución del juego (opcional).
     * @param month        Mes de ejecución del juego (opcional).
     * @param day          Día de ejecución del juego (opcional).
     * @return {@link ResponseEntity} con la lista de registros encontrados.
     */
    @GetMapping
    public ResponseEntity<List<RegistroJuegoResponseDto>> getStats(
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Long idResidencia,
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day){

        List<RegistroJuegoResponseDto> resultados = registroJuegoService.getStats(idResidente, idResidencia, idJuego, year, month, day);
        return ResponseEntity.ok(resultados);


    }

    /**
     * Elimina un registro de juego del sistema.
     * <p>
     * Este método recibe un ID de registro de juego como parámetro y solicita al servicio
     * {@link com.kevinolarte.resibenissa.services.RegistroJuegoService} que elimine la entidad correspondiente.
     * Si la eliminación es exitosa, se devuelve un DTO con los datos del registro eliminado.
     * </p>
     *
     * @param idRegistroJuego ID del registro de juego que se desea eliminar. Debe existir en el sistema.
     * @return {@link ResponseEntity} que contiene el DTO del registro eliminado y el estado HTTP 200 (OK).
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si el registro no existe.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<RegistroJuegoResponseDto> remove(@RequestParam Long idRegistroJuego){
        RegistroJuegoResponseDto registroTmp = registroJuegoService.remove(idRegistroJuego);
        return ResponseEntity.ok(registroTmp);
    }

}
