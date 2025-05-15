package com.kevinolarte.resibenissa.controllers.modulojuego.juego;


import com.kevinolarte.resibenissa.dto.in.modulojuego.JuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.JuegoResponseDto;
import com.kevinolarte.resibenissa.services.modulojuego.JuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Registra un nuevo juego asociado a una residencia.
     *
     * @param juegoDto Datos del juego a registrar.
     * @return {@link ResponseEntity} con los datos del juego creado y estado 201 CREATED.
     */
    @PostMapping("/add")
    public ResponseEntity<JuegoResponseDto> add(
            @RequestBody JuegoDto juegoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.save(juegoDto));

    }

    /**
     * Elimina un juego de una residencia si no tiene referencias dependientes.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego a eliminar.
     * @return {@link ResponseEntity} con estado 204 NO CONTENT si la eliminación es exitosa.
     */
    @DeleteMapping("/{idJuego}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego) {

        juegoService.delete(idJuego);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza los datos de un juego ya registrado en una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego a actualizar.
     * @param juegoDto Datos nuevos del juego.
     * @return {@link ResponseEntity} con los datos del juego actualizado.
     */
    @PatchMapping("/{idJuego}/update")
    public ResponseEntity<JuegoResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @RequestBody JuegoDto juegoDto) {
        return ResponseEntity.ok(juegoService.update(idJuego, juegoDto));
    }

}
