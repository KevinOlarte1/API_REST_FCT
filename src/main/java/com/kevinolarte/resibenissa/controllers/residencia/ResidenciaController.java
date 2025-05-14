package com.kevinolarte.resibenissa.controllers.residencia;

import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaPublicResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.ResidenciaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/resi")
@RestController
@AllArgsConstructor
public class ResidenciaController {

    private final ResidenciaService residenciaService;


    /**
     * Crea una nueva residencia.
     *
     * @param residenciaDto DTO con los datos de la residencia a crear.
     * @return {@link ResponseEntity} con estado {@code 201 Created} y el DTO de la residencia creada.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenciaResponseDto> add(@RequestBody ResidenciaDto residenciaDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();
        ResidenciaResponseDto residencia = residenciaService.save(residenciaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(residencia);

    }

    /**
     * Obtiene una residencia por su ID.
     *
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el DTO de la residencia encontrada.
     */
    @GetMapping("/get")
    public ResponseEntity<ResidenciaResponseDto> get() {
        System.out.println("Resi");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(residenciaService.get(currentUser.getResidencia().getId()));
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
     * Elimina una residencia de forma lógica. Dandolo de baja.
     *
     *
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @PatchMapping("/baja")
    public ResponseEntity<Void> baja() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        residenciaService.baja(currentUser.getResidencia().getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }







}
