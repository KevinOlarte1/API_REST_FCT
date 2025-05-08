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
 * Permite agregar nuevos registros, consultar estadísticas con múltiples filtros
 * y eliminar registros específicos.
 * </p>
 * <p>
 * Todas las rutas expuestas están bajo el prefijo <code>/resi/juegos/stats</code>.
 * </p>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/{idResidencia}/juego/{idJuego}")
@RestController
@AllArgsConstructor
public class RegistroJuegoController {

    private final RegistroJuegoService registroJuegoService;


    @PostMapping("/registro/add")
    public ResponseEntity<RegistroJuegoResponseDto> add(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto) {
        RegistroJuegoResponseDto registroJuego = registroJuegoService.add(idResidencia, idJuego, registroJuegoDto);
        return ResponseEntity.ok(registroJuego);

    }

    @GetMapping("/registro/{idRegistroJuego}/get")
    public ResponseEntity<RegistroJuegoResponseDto> get(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @PathVariable Long idRegistroJuego) {
        return ResponseEntity.ok(registroJuegoService.get(idResidencia, idJuego, idRegistroJuego));
    }

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

    @PatchMapping("/registro/{idRegistroJuego}/addComment")
    public ResponseEntity<RegistroJuegoResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @PathVariable Long idRegistroJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto){
        return ResponseEntity.ok(registroJuegoService.update(idResidencia, idJuego, idRegistroJuego, registroJuegoDto));
    }


    @DeleteMapping("/registro/{idRegistroJuego}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @PathVariable Long idRegistroJuego){
        registroJuegoService.delete(idResidencia, idJuego, idRegistroJuego);
        return ResponseEntity.noContent().build();
    }

}
