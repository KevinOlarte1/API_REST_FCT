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

/**
 * Controlador que expone endpoints REST para gestionar entidades {@link Residencia}.
 * <p>
 * Permite crear nuevas residencias, obtenerlas por ID y eliminarlas.
 * </p>
 *
 * URL Base: {@code /resi}
 *
 * @author : Kevin Olarte
 */
@RequestMapping("/resi")
@RestController
@AllArgsConstructor
public class ResidenciaController {

    private final ResidenciaService residenciaService;
    private final ResidenteService residenteService;



    @PostMapping("/add")
    public ResponseEntity<ResidenciaResponseDto> add(@RequestBody ResidenciaDto residenciaDto) {
            ResidenciaResponseDto residencia = residenciaService.save(residenciaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(residencia);

    }


    @GetMapping("/{idResidencia}/get")
    public ResponseEntity<ResidenciaResponseDto> get(
            @PathVariable Long idResidencia) {
        ResidenciaResponseDto residencias = residenciaService.get(idResidencia);
        return ResponseEntity.ok(residencias);
    }


    @DeleteMapping("/{idResidencia}/delete")
    public ResponseEntity<Void> remove(@PathVariable Long idResidencia){
        residenciaService.remove(idResidencia);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
