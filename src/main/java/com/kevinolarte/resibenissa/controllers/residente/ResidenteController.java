package com.kevinolarte.resibenissa.controllers.residente;

import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
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
 * Controlador REST que gestiona las operaciones relacionadas con los residentes de una residencia.
 * <p>
 * Permite registrar, consultar, actualizar y eliminar residentes, así como listar residentes filtrados.
 * </p>
 *
 * URL base: {@code /resi/{idResidencia}/resident}
 *
 * Autor: Kevin Olarte
 */
@RequestMapping("/resi/resident")
@RestController
@AllArgsConstructor
public class ResidenteController {
    private final ResidenteService residenteService;


    /**
     * Registra un nuevo residente en una residencia.
     *
     * @param residenteDto DTO con los datos del nuevo residente.
     * @return {@link ResponseEntity} con estado {@code 201 Created} y el residente creado.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenteResponseDto> add(
            @RequestBody ResidenteDto residenteDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        return ResponseEntity.status(HttpStatus.CREATED).body(residenteService.add(currentUser.getResidencia().getId(), residenteDto));

    }


    /**
     * Obtiene los detalles de un residente específico.
     *
     * @param idResidente ID del residente.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el residente encontrado.
     */
    @GetMapping("/{idResidente}/get")
    public ResponseEntity<ResidenteResponseDto> get(
            @PathVariable Long idResidente) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(residenteService.get(currentUser.getResidencia().getId(), idResidente));

    }


    @GetMapping("/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
                                                    @RequestParam(required = false) LocalDate fechaNacimiento,
                                                    @RequestParam(required = false) LocalDate minFNac,
                                                    @RequestParam(required = false) LocalDate maxFNac,
                                                    @RequestParam(required = false) Integer maxAge,
                                                    @RequestParam(required = false) Integer minAge,
                                                    @RequestParam(required = false) Long idJuego,
                                                    @RequestParam(required = false) Long idEvento) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(residenteService.getAll(currentUser.getResidencia().getId(),fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento));
    }

    /**
     * Lista todos los residentes dados de baja en una residencia.
     * @return {@link ResponseEntity} con la lista de residentes dados de baja.
     */
    @GetMapping("/getAll/bajas")
    public ResponseEntity<List<ResidenteResponseDto>> getAllBajas(
                                                    @RequestParam(required = false) LocalDate fecha,
                                                    @RequestParam(required = false) LocalDate minFecha,
                                                    @RequestParam(required = false) LocalDate maxFecha){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(residenteService.getAllBajas(currentUser.getResidencia().getId(),fecha, minFecha, maxFecha));
    }

    /**
     * Elimina un residente de una residencia de forma lógica.
     *
     * @param idResidente ID del residente a eliminar.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @PatchMapping("/{idResidente}/baja")
    public ResponseEntity<Void> deleteLogico(
            @PathVariable Long idResidente) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();
        residenteService.deleteLogico(currentUser.getResidencia().getId(),idResidente);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza parcialmente los datos de un residente.
     *
     * @param idResidente ID del residente a actualizar.
     * @param residenteDto DTO con los datos a actualizar.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el residente actualizado.
     */
    @PatchMapping("/{idResidente}/update")
    public ResponseEntity<ResidenteResponseDto> update(
            @PathVariable Long idResidente,
            @RequestBody ResidenteDto residenteDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(residenteService.update(currentUser.getResidencia().getId(), idResidente, residenteDto));
    }




}
