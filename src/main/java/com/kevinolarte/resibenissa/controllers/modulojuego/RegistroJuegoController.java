package com.kevinolarte.resibenissa.controllers.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.services.modulojuego.RegistroJuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los registros de juegos jugados por los residentes.
 * <p>
 * Permite agregar nuevos registros, consultar estadísticas con múltiples filtros,
 * actualizar observaciones y eliminar registros específicos.
 * </p>
 * <p>
 * Todas las rutas expuestas están bajo el prefijo <code>/resi/{idResidencia}/juego/{idJuego}</code>.
 * </p>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/{idResidencia}/juego/{idJuego}")
@RestController
@AllArgsConstructor
public class RegistroJuegoController {

    private final RegistroJuegoService registroJuegoService;


    /**
     * Crea un nuevo registro de juego para un residente en una residencia y juego específicos.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param registroJuegoDto Datos del registro a guardar.
     * @return El registro de juego creado.
     */
    @PostMapping("/registro/add")
    public ResponseEntity<RegistroJuegoResponseDto> add(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto) {
        RegistroJuegoResponseDto registroJuego = registroJuegoService.add(idResidencia, idJuego, registroJuegoDto);
        return ResponseEntity.ok(registroJuego);

    }

    /**
     * Obtiene un registro de juego específico mediante su ID.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro de juego.
     * @return El registro de juego solicitado.
     */
    @GetMapping("/registro/{idRegistroJuego}/get")
    public ResponseEntity<RegistroJuegoResponseDto> get(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @PathVariable Long idRegistroJuego) {
        return ResponseEntity.ok(registroJuegoService.get(idResidencia, idJuego, idRegistroJuego));
    }

    @GetMapping("/registro/getAll")
    public ResponseEntity<List<RegistroJuegoResponseDto>> getAll(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego) {
        return ResponseEntity.ok(registroJuegoService.getAll(idResidencia, idJuego));
    }

    /**
     * Obtiene todos los registros de juego filtrados por dificultad, y opcionalmente por residente, usuario, año y mes.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param dificultad Dificultad del juego (FACIL, MEDIO o DIFICIL).
     * @param idResidente (Opcional) ID del residente.
     * @param idUser (Opcional) ID del usuario (trabajador).
     * @param year (Opcional) Año de la partida.
     * @param month (Opcional) Mes de la partida.
     * @return Lista de registros de juego que coinciden con los filtros.
     */
    @GetMapping("/dificultad/{dificultad}/getAll")
    public ResponseEntity<List<RegistroJuegoResponseDto>> getAll(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @PathVariable Dificultad dificultad,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Long idUser,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month){
        return ResponseEntity.ok(registroJuegoService.getAll(idResidencia, idJuego, dificultad, idResidente, idUser, year, month));


    }

    /**
     * Actualiza un registro de juego existente añadiendo o modificando una observación.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro a actualizar.
     * @param registroJuegoDto DTO con los nuevos datos (principalmente observación).
     * @return Registro de juego actualizado.
     */
    @PatchMapping("/registro/{idRegistroJuego}/addComment")
    public ResponseEntity<RegistroJuegoResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @PathVariable Long idRegistroJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto){
        return ResponseEntity.ok(registroJuegoService.update(idResidencia, idJuego, idRegistroJuego, registroJuegoDto));
    }


    /**
     * Elimina un registro de juego específico.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro a eliminar.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     */
    @DeleteMapping("/registro/{idRegistroJuego}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @PathVariable Long idRegistroJuego){
        registroJuegoService.delete(idResidencia, idJuego, idRegistroJuego);
        return ResponseEntity.noContent().build();
    }

}
