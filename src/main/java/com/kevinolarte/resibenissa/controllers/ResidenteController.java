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


/**
 * Controlador REST que gestiona las operaciones relacionadas con los residentes del sistema.
 * <p>
 * Permite registrar nuevos residentes y recuperar residentes filtrando por residencia o ID individual.
 * Este controlador forma parte del módulo de gestión de residencias.
 * </p>
 *
 * Ruta base: <b>/resi/residents</b>
 *
 * @author Kevin
 */
@RequestMapping("/resi/residents")
@RestController
@AllArgsConstructor
public class ResidenteController {
    private final ResidenteService residenteService;

    /**
     * Registra un nuevo residente en una residencia existente.
     * <p>
     * Valida que los campos requeridos no estén vacíos, que la fecha de nacimiento sea válida
     * y que la residencia especificada exista.
     * </p>
     *
     * @param residenteDto DTO con los datos del residente a crear.
     * @return {@link ResponseEntity} con los datos del residente registrado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si hay errores de validación o la residencia no existe.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenteResponseDto> addResidente(@RequestBody ResidenteDto residenteDto)throws RuntimeException {
            ResidenteResponseDto residente = residenteService.save(residenteDto);
            return ResponseEntity.ok().body(residente);

    }

    /**
     * Recupera residentes del sistema, con opciones de filtrado.
     * <p>
     * Puede comportarse de las siguientes maneras según los parámetros:
     * <ul>
     *   <li>Sin parámetros: devuelve todos los residentes.</li>
     *   <li>Con <code>idResidencia</code>: devuelve todos los residentes asociados a esa residencia.</li>
     *   <li>Con <code>idResidente</code>: devuelve el residente con ese ID específico.</li>
     *   <li>Con ambos parámetros: aplica ambos filtros simultáneamente.</li>
     * </ul>
     * </p>
     *
     * @param idResidencia (opcional) ID de la residencia para filtrar los residentes.
     * @param idResidente (opcional) ID del residente específico a recuperar.
     * @return {@link ResponseEntity} con la lista de residentes o un único residente, dependiendo del filtro aplicado.
     */
    @GetMapping()
    public ResponseEntity<List<ResidenteResponseDto>> getResidente(
            @RequestParam(required = false) Long idResidencia,
            @RequestParam(required = false) Long idResidente) {

        return ResponseEntity.ok(residenteService.getResidentes(idResidencia, idResidente));

    }




}
