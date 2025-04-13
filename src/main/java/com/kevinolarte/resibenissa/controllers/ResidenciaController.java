package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.services.ResidenciaService;
import com.kevinolarte.resibenissa.services.ResidenteService;
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
    private final ResidenteService residenteService;

    @PostMapping("/add")
    public ResponseEntity<ResidenciaResponseDto> addResidencia(@RequestBody ResidenciaDto residenciaDto) {
            ResidenciaResponseDto residencia = residenciaService.save(residenciaDto);
            return ResponseEntity.ok(residencia);

    }

    @GetMapping()
    public ResponseEntity<List<ResidenciaResponseDto>> getResidencia(
            @RequestParam(required = false) Long idResidencia) {
        List<ResidenciaResponseDto> residencias = residenciaService.getResidencias(idResidencia);
        return ResponseEntity.ok(residencias);

    }

}
