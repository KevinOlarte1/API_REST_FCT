package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
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
    public ResponseEntity<ResidenteResponseDto> addResidente(@RequestBody ResidenteDto residenteDto)throws RuntimeException {
            ResidenteResponseDto residente = residenteService.save(residenteDto);
            return ResponseEntity.ok().body(residente);

    }

    @GetMapping()
    public ResponseEntity<?> getResidente(
            @RequestParam(required = false) Long idResidencia,
            @RequestParam(required = false) Long idResidente) {
        try {
            return ResponseEntity.ok().body(residenteService.getResidentes(idResidencia, idResidente));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




}
