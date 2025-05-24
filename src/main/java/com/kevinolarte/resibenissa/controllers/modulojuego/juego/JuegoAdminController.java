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
 * Ruta base: <b>/admin/resi/juego</b>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi/juego")
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
     * Obtiene los datos de un juego específico en una residencia.
     * @param idJuego ID del juego a consultar.
     * @return {@link ResponseEntity} con los datos del juego solicitado y estado 200 OK.
     */
    @GetMapping("/{idJuego}/get")
    public ResponseEntity<JuegoResponseDto> get(
                                @PathVariable Long idJuego) {
        return ResponseEntity.ok(juegoService.get(idJuego));
    }

    /**
     * Obtiene una lista de todos los juegos registrados en una residencia.
     *
     * @param nombreJuego Nombre del juego para filtrar (opcional).
     * @param maxRegistros Indica si se debe limitar la cantidad de registros devueltos (opcional).
     * @return {@link ResponseEntity} con la lista de juegos y estado 200 OK.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(
                                @RequestParam(required = false) String nombreJuego,
                                @RequestParam(required = false) Boolean maxRegistros) {
        return ResponseEntity.ok(juegoService.getAll(nombreJuego, maxRegistros));
    }

    /**
     * Elimina un juego de una residencia si no tiene referencias dependientes.
     *
     * @param idJuego ID del juego a eliminar.
     * @return {@link ResponseEntity} con estado 204 NO CONTENT si la eliminación es exitosa.
     */
    @DeleteMapping("/{idJuego}/delete")
    public ResponseEntity<Void> delete(
                                @PathVariable Long idJuego) {

        juegoService.delete(idJuego);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza los datos de un juego ya registrado en una residencia.
     *
     * @param idJuego ID del juego a actualizar.
     * @param juegoDto Datos nuevos del juego.
     * @return {@link ResponseEntity} con los datos del juego actualizado.
     */
    @PatchMapping("/{idJuego}/update")
    public ResponseEntity<JuegoResponseDto> update(
                                @PathVariable Long idJuego,
                                @RequestBody JuegoDto juegoDto) {
        return ResponseEntity.ok(juegoService.update(idJuego, juegoDto));
    }

}
