package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.ResidenteDto;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.services.ResidenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/resi/resident")
@RestController
@AllArgsConstructor
public class ResidenteController {
    private final ResidenteService residenteService;

    @PostMapping("/add")
    public ResponseEntity<?> addResidente(@RequestBody ResidenteDto residenteDto)throws RuntimeException {
        try{
            Residente residente = residenteService.save(residenteDto);
            return ResponseEntity.ok().body(residente);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getResidente(
            @RequestParam(required = false) Long resindeciaId,
            @RequestParam(required = false) Long residenteId) {
        try {
            if (residenteId != null)
                return ResponseEntity.ok(residenteService.findById(residenteId));

            if (resindeciaId != null)
                return ResponseEntity.ok(residenteService.findByResidencia(resindeciaId));
            else
                return ResponseEntity.ok(residenteService.findAll());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




}
