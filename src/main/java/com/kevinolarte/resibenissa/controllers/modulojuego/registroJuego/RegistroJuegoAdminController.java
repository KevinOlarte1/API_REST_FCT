package com.kevinolarte.resibenissa.controllers.modulojuego.registroJuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.modulojuego.RegistroJuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
@RequestMapping("/admin/resi")
@RestController
@AllArgsConstructor
public class RegistroJuegoAdminController {

    private final RegistroJuegoService registroJuegoService;
    /**
     * Crea un nuevo registro de juego para un residente en una residencia y juego específicos.
     *
     * @param idJuego ID del juego.
     * @param registroJuegoDto Datos del registro a guardar.
     * @return El registro de juego creado.
     */
    @PostMapping("/{idResidencia}/juego/{idJuego}/registro/add")
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
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro de juego.
     * @return El registro de juego solicitado.
     */
    @GetMapping("/{idResidencia}/juego/{idJuego}/registro/{idRegistroJuego}/get")
    public ResponseEntity<RegistroJuegoResponseDto> get(
                                                    @PathVariable Long idResidencia,
                                                    @PathVariable Long idJuego,
                                                    @PathVariable Long idRegistroJuego) {

        return ResponseEntity.ok(registroJuegoService.get(idResidencia, idJuego, idRegistroJuego));
    }

    /**
     * Obtiene una lista de registros de juego filtrados por varios parámetros.
     * @param idJuego ID del juego.
     * @param dificultad Dificultad del juego (opcional).
     * @param edad Edad del residente (opcional).
     * @param minEdad Edad mínima del residente (opcional).
     * @param maxEdad Edad máxima del residente (opcional).
     * @param idResidente ID del residente (opcional).
     * @param fecha Fecha del registro (opcional).
     * @param minFecha Fecha mínima del registro (opcional).
     * @param maxFecha Fecha máxima del registro (opcional).
     * @param promedio true si se desea obtener el promedio de los registros, false en caso contrario.
     * @param masPromedio true si se desea obtener los registros con puntajes superiores al promedio, false en caso contrario.
     * @param menosPromedio true si se desea obtener los registros con puntajes inferiores al promedio, false en caso contrario.
     * @param ordenFecha true si se desea ordenar por fecha, false en caso contrario.
     * @return Lista de registros de juego filtrados.
     */
    @GetMapping("/juego/{idJuego}/registro/getAll")
    public ResponseEntity<List<RegistroJuegoResponseDto>> getAll(
                                                        @PathVariable Long idJuego,
                                                        @RequestParam(required = false) Dificultad dificultad,
                                                        @RequestParam(required = false) Integer edad,
                                                        @RequestParam(required = false) Integer minEdad,
                                                        @RequestParam(required = false) Integer maxEdad,
                                                        @RequestParam(required = false) Long idResidente,
                                                        @RequestParam(required = false) LocalDate fecha,
                                                        @RequestParam(required = false) LocalDate minFecha,
                                                        @RequestParam(required = false) LocalDate maxFecha,
                                                        @RequestParam(required = false) boolean promedio,
                                                        @RequestParam(required = false) boolean masPromedio,
                                                        @RequestParam(required = false) boolean menosPromedio,
                                                        @RequestParam(required = false) boolean ordenFecha,
                                                        @RequestParam(required = false) Boolean comentado){

        return ResponseEntity.ok(registroJuegoService.getAll(idJuego, dificultad, edad, minEdad, maxEdad, idResidente, fecha, minFecha, maxFecha, promedio, masPromedio, menosPromedio, ordenFecha,comentado));
    }

    /**
     * Obtiene una lista de registros de juego filtrados por varios parámetros de una residencia especifica
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param dificultad Dificultad del juego (opcional).
     * @param edad Edad del residente (opcional).
     * @param minEdad Edad mínima del residente (opcional).
     * @param maxEdad Edad máxima del residente (opcional).
     * @param idResidente ID del residente (opcional).
     * @param fecha Fecha del registro (opcional).
     * @param minFecha Fecha mínima del registro (opcional).
     * @param maxFecha Fecha máxima del registro (opcional).
     * @param promedio true si se desea obtener el promedio de los registros, false en caso contrario.
     * @param masPromedio true si se desea obtener los registros con puntajes superiores al promedio, false en caso contrario.
     * @param menosPromedio true si se desea obtener los registros con puntajes inferiores al promedio, false en caso contrario.
     * @param ordenFecha true si se desea ordenar por fecha, false en caso contrario.
     * @return Lista de registros de juego filtrados.
     */
    @GetMapping("/{idResidencia}/juego/{idJuego}/registro/getAll")
    public ResponseEntity<List<RegistroJuegoResponseDto>> getAll(
                                                        @PathVariable Long idResidencia,
                                                        @PathVariable Long idJuego,
                                                        @RequestParam(required = false) Dificultad dificultad,
                                                        @RequestParam(required = false) Integer edad,
                                                        @RequestParam(required = false) Integer minEdad,
                                                        @RequestParam(required = false) Integer maxEdad,
                                                        @RequestParam(required = false) Long idResidente,
                                                        @RequestParam(required = false) LocalDate fecha,
                                                        @RequestParam(required = false) LocalDate minFecha,
                                                        @RequestParam(required = false) LocalDate maxFecha,
                                                        @RequestParam(required = false) boolean promedio,
                                                        @RequestParam(required = false) boolean masPromedio,
                                                        @RequestParam(required = false) boolean menosPromedio,
                                                        @RequestParam(required = false) boolean ordenFecha,
                                                        @RequestParam(required = false) Boolean comentado){
        return ResponseEntity.ok(registroJuegoService.getAll(idResidencia, idJuego, dificultad, edad, minEdad, maxEdad, idResidente, fecha, minFecha, maxFecha, promedio, masPromedio, menosPromedio, ordenFecha, comentado));
    }

    /**
     * Actualiza un registro de juego existente añadiendo o modificando una observación.
     *
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro a actualizar.
     * @param registroJuegoDto DTO con los nuevos datos (principalmente observación).
     * @return Registro de juego actualizado.
     */
    @PatchMapping("/{idResidencia}/juego/{idJuego}/registro/{idRegistroJuego}/addComment")
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
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro a eliminar.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     */
    @DeleteMapping("/{idResidencia}/juego/{idJuego}/registro/{idRegistroJuego}/delete")
    public ResponseEntity<Void> delete(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idJuego,
                                @PathVariable Long idRegistroJuego){
        registroJuegoService.delete(idResidencia, idJuego, idRegistroJuego);
        return ResponseEntity.noContent().build();
    }


}
