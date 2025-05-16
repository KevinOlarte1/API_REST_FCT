package com.kevinolarte.resibenissa.controllers.residente;

import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
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
@RequestMapping("/admin/resi/")
@RestController
@AllArgsConstructor
public class ResidenteAdminController {
    private final ResidenteService residenteService;


    /**
     * Registra un nuevo residente en una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param residenteDto DTO con los datos del nuevo residente.
     * @return {@link ResponseEntity} con estado {@code 201 Created} y el residente creado.
     */
    @PostMapping("/{idResidencia}/resident/add")
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
    @GetMapping("/{idResidencia}/resident/{idResidente}/get")
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
    @GetMapping("/{idResidencia}/resident/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
                                                    @PathVariable Long idResidencia,
                                                    @RequestParam(required = false) String documentoIdentidad,
                                                    @RequestParam(required = false) LocalDate fechaNacimiento,
                                                    @RequestParam(required = false) Integer year,
                                                    @RequestParam(required = false) Integer month,
                                                    @RequestParam(required = false) Integer maxAge,
                                                    @RequestParam(required = false) Integer minAge,
                                                    @RequestParam(required = false) Long idJuego,
                                                    @RequestParam(required = false) Long idEventoSalida,
                                                    @RequestParam(required = false) Long minRegistro,
                                                    @RequestParam(required = false) Long maxRegistro) {

        return ResponseEntity.ok(residenteService.getAll(idResidencia,fechaNacimiento, year, month, maxAge, minAge, documentoIdentidad, idJuego, idEventoSalida, minRegistro, maxRegistro));
    }
    @GetMapping("/resident/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
                                                    @RequestParam(required = false) String documentoIdentidad,
                                                    @RequestParam(required = false) LocalDate fechaNacimiento,
                                                    @RequestParam(required = false) Integer year,
                                                    @RequestParam(required = false) Integer month,
                                                    @RequestParam(required = false) Integer maxAge,
                                                    @RequestParam(required = false) Integer minAge,
                                                    @RequestParam(required = false) Long idJuego,
                                                    @RequestParam(required = false) Long idEventoSalida,
                                                    @RequestParam(required = false) Long minRegistro,
                                                    @RequestParam(required = false) Long maxRegistro){
        return ResponseEntity.ok(residenteService.getAll(fechaNacimiento, year, month, maxAge, minAge, documentoIdentidad, idJuego, idEventoSalida, minRegistro, maxRegistro));
    }

    /**
     * Lista todos los residentes dados de baja en una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @return {@link ResponseEntity} con la lista de residentes dados de baja.
     */
    @GetMapping("/{idResidencia}/resident/getAll/bajas")
    public ResponseEntity<List<ResidenteResponseDto>> getAllBajas(
                                                    @PathVariable Long idResidencia,
                                                    @RequestParam(required = false) String documentoIdentidad){
        return ResponseEntity.ok(residenteService.getAllBajas(idResidencia, documentoIdentidad));
    }

    /**
     * Lista todos los residentes dados de baja.
     *
     * @return {@link ResponseEntity} con la lista de residentes dados de baja.
     */
    @GetMapping("/resident/getAll/bajas")
    public ResponseEntity<List<ResidenteResponseDto>> getAllBajas(
                                                    @RequestParam(required = false) String documentoIdentidad){
        return ResponseEntity.ok(residenteService.getAllBajas(documentoIdentidad));
    }

    /**
     * Elimina un residente de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente a eliminar.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @DeleteMapping("/{idResidencia}/resident/{idResidente}/delete")
    public ResponseEntity<Void> delete(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idResidente) {

        residenteService.deleteFisico(idResidencia,idResidente);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Elimina un residente de una residencia de forma lógica.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente a eliminar.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @PatchMapping("{idResidencia}/resident/{idResidente}/baja")
    public ResponseEntity<Void> deleteLogico(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idResidente) {
        residenteService.deleteLogico(idResidencia,idResidente);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza parcialmente los datos de un residente.
     *
     * @param idResidente ID del residente a actualizar.
     * @param residenteDto DTO con los datos a actualizar.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el residente actualizado.
     */
    @PatchMapping("{idResidencia}/resident/{idResidente}/update")
    public ResponseEntity<ResidenteResponseDto> update(
                                                @PathVariable Long idResidencia,
                                                @PathVariable Long idResidente,
                                                @RequestBody ResidenteDto residenteDto) {
        return ResponseEntity.ok(residenteService.update(idResidencia, idResidente, residenteDto));
    }
}
