package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.JuegoDto;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.services.JuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/resi/juego")
@AllArgsConstructor
public class JuegoController {
    private final JuegoService juegoService;

    @PostMapping("/add")
    public ResponseEntity<?> addJuego(@RequestBody JuegoDto juegoDto) {
        try{
            Juego juego = juegoService.save(juegoDto);
            return ResponseEntity.ok(juego);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
