package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.services.ResidenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


/**
 * Controlador REST que gestiona las operaciones relacionadas con los residentes de una residencia.
 * <p>
 * Permite registrar, consultar, actualizar y eliminar residentes, así como listar residentes filtrados.
 * </p>
 *
 * URL base: {@code /resi/{idResidencia}/resident}
 *
 * Autor: Kevin Olarte
 */
@RequestMapping("/resi/{idResidencia}/resident")
@RestController
@AllArgsConstructor
public class ResidenteController {
    private final ResidenteService residenteService;


    /**
     * Registra un nuevo residente en una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param residenteDto DTO con los datos del nuevo residente.
     * @return {@link ResponseEntity} con estado {@code 201 Created} y el residente creado.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenteResponseDto> add(
            @PathVariable Long idResidencia,
            @RequestBody ResidenteDto residenteDto){

        return ResponseEntity.status(HttpStatus.CREATED).body(residenteService.add(idResidencia, residenteDto));

    }


    /**
     * Obtiene los detalles de un residente específico.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el residente encontrado.
     */
    @GetMapping("/{idResidente}/get")
    public ResponseEntity<ResidenteResponseDto> get(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente) {

        return ResponseEntity.ok(residenteService.get(idResidencia, idResidente));

    }

    /**
     * Lista todos los residentes de una residencia, aplicando filtros opcionales.
     *
     * @param idResidencia ID de la residencia.
     * @param documentoIdentidad Número de documento de identidad del residente (opcional).
     * @param fechaNacimiento Fecha de nacimiento del residente (opcional).
     * @param year Año de nacimiento del residente (opcional).
     * @param month Mes de nacimiento del residente (opcional).
     * @return {@link ResponseEntity} con la lista de residentes encontrados.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
            @PathVariable Long idResidencia,
            @RequestParam(required = false) String documentoIdentidad,
            @RequestParam(required = false) LocalDate fechaNacimiento,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Long idEventoSalida){

        return ResponseEntity.ok(residenteService.getAll(idResidencia,fechaNacimiento, year, month, documentoIdentidad, idJuego, idEventoSalida));
    }

    /**
     * Elimina un residente de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente a eliminar.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @DeleteMapping("/{idResidente}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente) {
        residenteService.delete(idResidencia,idResidente);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza parcialmente los datos de un residente.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente a actualizar.
     * @param residenteDto DTO con los datos a actualizar.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el residente actualizado.
     */
    @PatchMapping("/{idResidente}/update")
    public ResponseEntity<ResidenteResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente,
            @RequestBody ResidenteDto residenteDto) {

        return ResponseEntity.ok(residenteService.update(idResidencia, idResidente, residenteDto));
    }




}
