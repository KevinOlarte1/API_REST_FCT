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

@RequestMapping("/resi/juegos/stats")
@RestController
@AllArgsConstructor
public class RegistroJuegoController {

    private final RegistroJuegoService registroJuegoService;

    @PostMapping("/add")
    public ResponseEntity<RegistroJuegoResponseDto> addRegistroJuego(@RequestBody RegistroJuegoDto registroJuegoDto) {
        RegistroJuegoResponseDto registroJuego = registroJuegoService.save(registroJuegoDto);
        return ResponseEntity.ok(registroJuego);

    }

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

    @DeleteMapping("/remove")
    public ResponseEntity<RegistroJuegoResponseDto> remove(@RequestParam Long idRegistroJuego){
        RegistroJuegoResponseDto registroTmp = registroJuegoService.remove(idRegistroJuego);
        return ResponseEntity.ok(registroTmp);
    }

}
