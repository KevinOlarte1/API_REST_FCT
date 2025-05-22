package com.kevinolarte.resibenissa.controllers.residente;

import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.in.moduloReporting.EmailRequestDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.ResidenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la administración de residentes dentro de una residencia.
 * <p>
 * Permite registrar, consultar, actualizar, eliminar y listar residentes
 * aplicando filtros opcionales, tanto a nivel de una residencia específica como global.
 * </p>
 *
 * URL base: {@code /admin/resi}
 *
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi/")
@RestController
@AllArgsConstructor
public class ResidenteAdminController {
    private final ResidenteService residenteService;


    /**
     * Registra un nuevo residente dentro de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param residenteDto DTO con los datos del residente a crear.
     * @return {@link ResponseEntity} con estado {@code 201 Created} y el residente creado.
     */
    @PostMapping("/{idResidencia}/resident/add")
    public ResponseEntity<ResidenteResponseDto> add(
                                    @PathVariable Long idResidencia,
                                    @RequestBody ResidenteDto residenteDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(residenteService.add(idResidencia, residenteDto));

    }

    /**
     * Obtiene los detalles de un residente específico dentro de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y los datos del residente.
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
     * @param fechaNacimiento Fecha exacta de nacimiento.
     * @param minFNac Fecha mínima de nacimiento.
     * @param maxFNac Fecha máxima de nacimiento.
     * @param maxAge Edad máxima.
     * @param minAge Edad mínima.
     * @param idJuego ID de juego asociado (opcional).
     * @param idEvento ID de evento asociado (opcional).
     * @return {@link ResponseEntity} con la lista de residentes filtrados.
     */
    @GetMapping("/{idResidencia}/resident/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
                                    @PathVariable Long idResidencia,
                                    @RequestParam(required = false) LocalDate fechaNacimiento,
                                    @RequestParam(required = false) LocalDate minFNac,
                                    @RequestParam(required = false) LocalDate maxFNac,
                                    @RequestParam(required = false) Integer maxAge,
                                    @RequestParam(required = false) Integer minAge,
                                    @RequestParam(required = false) Long idJuego,
                                    @RequestParam(required = false) Long idEvento) {

        return ResponseEntity.ok(residenteService.getAll(idResidencia, fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento));
    }

    /**
     * Lista todos los residentes en el sistema, sin filtrar por residencia.
     *
     * @param fechaNacimiento Fecha exacta de nacimiento.
     * @param minFNac Fecha mínima de nacimiento.
     * @param maxFNac Fecha máxima de nacimiento.
     * @param maxAge Edad máxima.
     * @param minAge Edad mínima.
     * @param idJuego ID de juego asociado (opcional).
     * @param idEvento ID de evento asociado (opcional).
     * @return {@link ResponseEntity} con la lista global de residentes filtrados.
     */
    @GetMapping("/resident/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
                                    @RequestParam(required = false) LocalDate fechaNacimiento,
                                    @RequestParam(required = false) LocalDate minFNac,
                                    @RequestParam(required = false) LocalDate maxFNac,
                                    @RequestParam(required = false) Integer maxAge,
                                    @RequestParam(required = false) Integer minAge,
                                    @RequestParam(required = false) Long idJuego,
                                    @RequestParam(required = false) Long idEvento){
        return ResponseEntity.ok(residenteService.getAll(fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento));
    }

    /**
     * Lista todos los residentes dados de baja dentro de una residencia, filtrando opcionalmente por fecha.
     *
     * @param idResidencia ID de la residencia.
     * @param fecha Fecha exacta de baja (opcional).
     * @param minFecha Fecha mínima de baja (opcional).
     * @param maxFecha Fecha máxima de baja (opcional).
     * @return {@link ResponseEntity} con la lista de residentes dados de baja.
     */
    @GetMapping("/{idResidencia}/resident/getAll/bajas")
    public ResponseEntity<List<ResidenteResponseDto>> getAllBajas(
                                    @PathVariable Long idResidencia,
                                    @RequestParam(required = false) LocalDate fecha,
                                    @RequestParam(required = false) LocalDate minFecha,
                                    @RequestParam(required = false) LocalDate maxFecha){

        return ResponseEntity.ok(residenteService.getAllBajas(idResidencia, fecha, minFecha, maxFecha));
    }

    /**
     * Lista todos los residentes dados de baja del sistema (sin filtro de residencia).
     *
     * @param fecha Fecha exacta de baja (opcional).
     * @param minFecha Fecha mínima de baja (opcional).
     * @param maxFecha Fecha máxima de baja (opcional).
     * @return {@link ResponseEntity} con la lista global de residentes dados de baja.
     */
    @GetMapping("/resident/getAll/bajas")
    public ResponseEntity<List<ResidenteResponseDto>> getAllBajas(
                                    @RequestParam(required = false) LocalDate fecha,
                                    @RequestParam(required = false) LocalDate minFecha,
                                    @RequestParam(required = false) LocalDate maxFecha){
        return ResponseEntity.ok(residenteService.getAllBajas(fecha, minFecha, maxFecha));
    }

    /**
     * Elimina físicamente un residente de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
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
     * Marca lógicamente como dado de baja a un residente (sin eliminarlo físicamente).
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si se realizó correctamente.
     */
    @PatchMapping("{idResidencia}/resident/{idResidente}/baja")
    public ResponseEntity<Void> deleteLogico(
                                    @PathVariable Long idResidencia,
                                    @PathVariable Long idResidente) {
        residenteService.deleteLogico(idResidencia,idResidente);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza parcialmente los datos de un residente dentro de una residencia.
     *
     * @param idResidencia ID de la residencia.
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

    /**
     * Envía un correo electrónico a un familiar de un residente.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @param emailRequestDto DTO con los datos del correo a enviar.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si el envío fue exitoso.
     */
    @PostMapping("/{idResidencia}/resident/{idResidente}/sendEmailFamily")
    public ResponseEntity<Void> sendEmailFamily(
                                    @PathVariable Long idResidencia,
                                    @PathVariable Long idResidente,
                                    @RequestBody EmailRequestDto emailRequestDto) {

        residenteService.sendEmailFamiliar(idResidencia, idResidente, emailRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
