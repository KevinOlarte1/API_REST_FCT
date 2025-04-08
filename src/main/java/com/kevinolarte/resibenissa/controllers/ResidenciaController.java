package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.ResidenciaDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.services.ResidenciaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/resi")
@RestController
@AllArgsConstructor
public class ResidenciaController {

    private final ResidenciaService residenciaService;

    @PostMapping("/add")
    public ResponseEntity<?> addResidencia(@RequestBody ResidenciaDto residenciaDto) {
        try{
            residenciaDto.setEmail(residenciaDto.getEmail().toLowerCase().trim());
            Residencia residencia = residenciaService.save(residenciaDto);
            return ResponseEntity.ok(residencia);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok(residenciaService.findAll());
    }

}
