package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.JuegoDto;
import com.kevinolarte.resibenissa.dto.RegistroJuegoDto;
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
    public ResponseEntity<?> addRegistroJuego(@RequestBody RegistroJuegoDto registroJuegoDto) {
        try{
            RegistroJuego registroJuego = registroJuegoService.save(registroJuegoDto);
            return ResponseEntity.ok(registroJuego);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getStats(
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Long idResidencia,
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day){
        try {
            List<RegistroJuego> resultados = registroJuegoService.getStats(idResidente, idResidencia, idJuego, year, month, day);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
