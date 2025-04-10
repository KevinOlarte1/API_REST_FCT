package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.JuegoDto;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.services.JuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/resi/juegos")
@RestController
@AllArgsConstructor
public class JuegoController {
    private final JuegoService juegoService;

    @PostMapping("/add")
    public ResponseEntity<?> addJuego(@RequestBody JuegoDto juegoDto) {
        System.out.println("ENTRA AL MENOS");
        try{
            Juego juego = juegoService.save(juegoDto);
            return ResponseEntity.ok(juego);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getJuego(
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Long idResidencia) {
        try{
            List<Juego> juegos = juegoService.getJuegos(idJuego, idResidencia);
            return ResponseEntity.ok(juegos);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
