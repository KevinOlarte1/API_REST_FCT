package com.kevinolarte.resibenissa.controllers.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.JuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.JuegoResponseDto;
import com.kevinolarte.resibenissa.services.modulojuego.JuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/resi/{idResidencia}/juego")
@RestController
@AllArgsConstructor
public class JuegoController {
    private final JuegoService juegoService;


    @PostMapping("/add")
    public ResponseEntity<JuegoResponseDto> add(
            @PathVariable Long idResidencia,
            @RequestBody JuegoDto juegoDto) {

            return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.save(idResidencia,juegoDto));

    }

    @GetMapping("/{idJuego}/get")
    public ResponseEntity<JuegoResponseDto> get(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego) {

        return ResponseEntity.ok(juegoService.get(idJuego, idResidencia));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<JuegoResponseDto>> getAll(
            @PathVariable Long idResidencia,
            @RequestParam(required = false) String nombreJuego,
            @RequestParam(required = false) boolean maxRegistros) {
        return ResponseEntity.ok(juegoService.getAll(idResidencia, nombreJuego, maxRegistros));
    }


    @DeleteMapping("/{idJuego}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego) {
        juegoService.delete(idResidencia,idJuego);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{idJuego}/update")
    public ResponseEntity<JuegoResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idJuego,
            @RequestBody JuegoDto juegoDto) {
        return ResponseEntity.ok(juegoService.update(idResidencia, idJuego, juegoDto));
    }




}
