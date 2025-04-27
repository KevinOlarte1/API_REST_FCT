package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.services.ResidenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RequestMapping("/resi/{idResidencia}/resident")
@RestController
@AllArgsConstructor
public class ResidenteController {
    private final ResidenteService residenteService;


    @PostMapping("/add")
    public ResponseEntity<ResidenteResponseDto> add(
            @PathVariable Long idResidencia,
            @RequestBody ResidenteDto residenteDto){

        ResidenteResponseDto residente = residenteService.add(idResidencia, residenteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(residente);

    }


    @GetMapping("/{idResidente}/get")
    public ResponseEntity<ResidenteResponseDto> get(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente) {

        return ResponseEntity.ok(residenteService.get(idResidencia, idResidente));

    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
            @PathVariable Long idResidencia,
            @RequestBody(required =false) ResidenteDto filtre){

        return ResponseEntity.ok(residenteService.getAll(idResidencia,filtre));
    }

    @DeleteMapping("/{idResidente}/delete")
    public ResponseEntity<Void> remove(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente) {
        residenteService.delete(idResidencia,idResidente);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{idResidente}/update")
    public ResponseEntity<ResidenteResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente,
            @RequestBody ResidenteDto residenteDto) {

        return ResponseEntity.ok(residenteService.update(idResidencia, idResidente, residenteDto));
    }




}
