package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.JuegoDto;
import com.kevinolarte.resibenissa.dto.out.JuegoResponseDto;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.services.JuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gestiona las operaciones relacionadas con los juegos.
 * <p>
 * Permite registrar nuevos juegos y consultar juegos existentes, con filtros por ID o por residencia.
 * </p>
 *
 * Ruta base: <b>/resi/juegos</b>
 *
 * @author Kevin
 */
@RequestMapping("/resi/juegos")
@RestController
@AllArgsConstructor
public class JuegoController {
    private final JuegoService juegoService;


    /**
     * Registra un nuevo juego en una residencia existente.
     * <p>
     * Valida que el nombre y la residencia estén presentes y que no exista un juego duplicado
     * con el mismo nombre en la misma residencia.
     * </p>
     *
     * @param juegoDto DTO con los datos del juego a crear.
     * @return {@link ResponseEntity} con los datos del juego registrado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si hay errores de validación o de integridad.
     */
    @PostMapping("/add")
    public ResponseEntity<JuegoResponseDto> addJuego(@RequestBody JuegoDto juegoDto) {
            JuegoResponseDto juego = juegoService.save(juegoDto);
            return ResponseEntity.ok(juego);

    }

    /**
     * Recupera juegos registrados en el sistema, aplicando filtros opcionales.
     * <p>
     * Comportamiento según los parámetros proporcionados:
     * <ul>
     *   <li>Sin parámetros: devuelve todos los juegos existentes.</li>
     *   <li>Con <code>idJuego</code>: devuelve el juego con ese ID, si existe.</li>
     *   <li>Con <code>idResidencia</code>: devuelve todos los juegos pertenecientes a esa residencia.</li>
     *   <li>Con ambos parámetros: aplica ambos filtros si es compatible (aunque generalmente <code>idJuego</code> es único).</li>
     * </ul>
     * </p>
     *
     * @param idJuego ID específico del juego a buscar (opcional).
     * @param idResidencia ID de la residencia para filtrar juegos (opcional).
     * @return {@link ResponseEntity} con una lista de juegos según los filtros aplicados.
     */
    @GetMapping
    public ResponseEntity<List<JuegoResponseDto>> getJuego(
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Long idResidencia) {

        List<JuegoResponseDto> juegos = juegoService.getJuegos(idJuego, idResidencia);
        return ResponseEntity.ok(juegos);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<JuegoResponseDto> remove(@RequestParam Long idJuego) {
        JuegoResponseDto juegoTmp = juegoService.remove(idJuego);
        return ResponseEntity.ok(juegoTmp);
    }



}
