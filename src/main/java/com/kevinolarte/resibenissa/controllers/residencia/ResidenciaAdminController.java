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
@RequestMapping("/resi/{idResidencia}")
@RestController
@AllArgsConstructor
public class ResidenciaAdminController {

    private final ResidenciaService residenciaService;



    /**
     * Obtiene una residencia por su ID.
     *
     * @param idResidencia ID de la residencia a recuperar.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el DTO de la residencia encontrada.
     */
    @GetMapping("/get")
    public ResponseEntity<ResidenciaResponseDto> get(
            @PathVariable Long idResidencia) {
        return ResponseEntity.ok(residenciaService.get(idResidencia));
    }


    /**
     * Elimina una residencia por su ID.
     *
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> remove(@PathVariable Long idResidencia) {
        residenciaService.remove(idResidencia);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Elimina una residencia de forma lógica. Dandolo de baja.
     *
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     */
    @PatchMapping("/baja")
    public ResponseEntity<Void> baja(
            @PathVariable Long idResidencia) {
        residenciaService.baja(idResidencia);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }




}

