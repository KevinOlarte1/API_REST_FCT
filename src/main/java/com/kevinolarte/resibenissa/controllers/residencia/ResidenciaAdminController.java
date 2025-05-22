package com.kevinolarte.resibenissa.controllers.residencia;


import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaPublicResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.services.ResidenciaService;
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
 * Autor: Kevin Olarte
 */
@RequestMapping("/admin/resi/")
@RestController
@AllArgsConstructor
public class ResidenciaAdminController {

    private final ResidenciaService residenciaService;



    /**
     * Crea una nueva residencia.
     *
     * @param residenciaDto DTO con los datos de la residencia a crear.
     * @return {@link ResponseEntity} con estado {@code 201 Created} y el DTO de la residencia creada.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenciaResponseDto> add(
                                @RequestBody ResidenciaDto residenciaDto) {
        ResidenciaResponseDto residencia = residenciaService.add(residenciaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(residencia);

    }

    /**
     * Obtiene una residencia por su ID.
     *
     * @param idResidencia ID de la residencia a recuperar.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el DTO de la residencia encontrada.
     */
    @GetMapping("/{idResidencia}/get")
    public ResponseEntity<ResidenciaResponseDto> get(
                                @PathVariable Long idResidencia) {
        return ResponseEntity.ok(residenciaService.get(idResidencia));
    }

    /**
     * Obtiene todas las residencias.
     *
     * @return {@link ResponseEntity} con estado {@code 200 OK} y una lista de DTOs de residencias.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ResidenciaPublicResponseDto>> getAll() {
        return ResponseEntity.ok(residenciaService.getAll());
    }

    /**
     * Obtiene todos los que estan de baja
     * @return {@link ResponseEntity} con estado {@code 200 OK} y una lista de DTOs de residencias.
     */
    @GetMapping("/getAll/baja")
    public ResponseEntity<List<ResidenciaPublicResponseDto>> getAllBaja() {
        return ResponseEntity.ok(residenciaService.getAllBaja());
    }

    /**
     * Elimina una residencia por su ID.
     *
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @DeleteMapping("/{idResidencia}/delete")
    public ResponseEntity<Void> remove(
                                @PathVariable Long idResidencia) {
        residenciaService.deleteFisico(idResidencia);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Elimina una residencia de forma lógica. Dandolo de baja.
     *
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @PatchMapping("/{idResidencia}/baja")
    public ResponseEntity<Void> baja(
                                @PathVariable Long idResidencia) {
        residenciaService.deleteLogico(idResidencia);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }




}

