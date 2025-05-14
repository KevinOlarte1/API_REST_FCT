package com.kevinolarte.resibenissa.controllers.modulojuego;


import com.kevinolarte.resibenissa.services.modulojuego.JuegoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/resi/{idResidencia}/juego")
@RestController
@AllArgsConstructor
public class JuegoAdminController {
    private final JuegoService juegoService;
}
