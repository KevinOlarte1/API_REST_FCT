package com.kevinolarte.resibenissa.controllers.modulojuego.juego;

import com.kevinolarte.resibenissa.dto.out.modulojuego.JuegoResponseDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.modulojuego.JuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controlador REST que gestiona las operaciones relacionadas con los juegos en una residencia.
 * <p>
 * Permite registrar, consultar, listar, actualizar y eliminar juegos asociados a una residencia.
 * </p>
 *
 * Ruta base: <b>/resi/{idResidencia}/juego</b>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/juego")
@RestController
@AllArgsConstructor
public class JuegoController {
    private final JuegoService juegoService;


    /**
     * Obtiene los datos de un juego específico dentro de una residencia.
     *
     * @param idJuego ID del juego a consultar.
     * @return {@link ResponseEntity} con los datos del juego solicitado.
     */
    @GetMapping("/{idJuego}/get")
    public ResponseEntity<JuegoResponseDto> get(
                                            @PathVariable Long idJuego) {

        return ResponseEntity.ok(juegoService.get(idJuego));
    }

    /**
     * Obtiene una lista de juegos de una residencia, con filtros opcionales por nombre y cantidad máxima.
     *
     * @param nombreJuego Filtro por nombre del juego (opcional).
     * @param maxRegistros Si es {@code true}, limita los resultados a un número máximo (definido en servicio).
     * @return {@link ResponseEntity} con la lista de juegos.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<JuegoResponseDto>> getAll(
                                                @RequestParam(required = false) String nombreJuego,
                                                @RequestParam(required = false) Boolean maxRegistros) {
        return ResponseEntity.ok(juegoService.getAll(nombreJuego, maxRegistros));
    }









}
