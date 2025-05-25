package com.kevinolarte.resibenissa.controllers.residencia;


import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaPublicResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
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
 * URL Base: {@code /admin/resi}
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
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenciaResponseDto> add(
                                @RequestBody ResidenciaDto residenciaDto) {
        ResidenciaResponseDto residencia;
        try {
            residencia = residenciaService.add(residenciaDto);
        } catch (ResiException e) {
            // Manejo de excepciones específicas de la aplicación
            throw new ApiException(e, null);
        } catch (Exception e) {
            // Manejo de excepciones genéricas
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
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
        ResidenciaResponseDto residencia;
        try {
            residencia = residenciaService.get(idResidencia);
        } catch (ResiException e) {
            // Manejo de excepciones específicas de la aplicación
            throw new ApiException(e, null);
        } catch (Exception e) {
            // Manejo de excepciones genéricas
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
        return ResponseEntity.ok(residencia);
    }

    /**
     * Obtiene todas las residencias.
     *
     * @return {@link ResponseEntity} con estado {@code 200 OK} y una lista de DTOs de residencias.
     * @throws ApiException si ocurre un error al intentar obtener las residencias.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ResidenciaPublicResponseDto>> getAll() {
        List<ResidenciaPublicResponseDto> residencias;
        try {
            residencias = residenciaService.getAll();
        } catch (Exception e) {
            // Manejo de excepciones genéricas
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
        return ResponseEntity.ok(residencias);
    }

    /**
     * Obtiene todos los que estan de baja
     * @return {@link ResponseEntity} con estado {@code 200 OK} y una lista de DTOs de residencias.
     * @throws ApiException si ocurre un error al intentar obtener las residencias.
     */
    @GetMapping("/getAll/baja")
    public ResponseEntity<List<ResidenciaPublicResponseDto>> getAllBaja() {
        List<ResidenciaPublicResponseDto> residencias;
        try {
            residencias = residenciaService.getAllBaja();
        } catch (Exception e) {
            // Manejo de excepciones genéricas
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
        return ResponseEntity.ok(residencias);
    }

    /**
     * Elimina una residencia por su ID.
     *
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     * @param idResidencia ID de la residencia a eliminar.
     * @throws ApiException si ocurre un error al intentar eliminar la residencia.
     */
    @DeleteMapping("/{idResidencia}/delete")
    public ResponseEntity<Void> remove(
                                @PathVariable Long idResidencia) {
        try {
            residenciaService.deleteFisico(idResidencia);
        }catch (ResiException e) {
            // Manejo de excepciones específicas de la aplicación
            throw new ApiException(e, null);
        } catch (Exception e) {
            // Manejo de excepciones genéricas
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Elimina una residencia de forma lógica. Dandolo de baja.
     *
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si la eliminación fue exitosa.
     * @param idResidencia ID de la residencia a dar de baja.
     * @throws ApiException si ocurre un error al intentar eliminar la residencia.
     */
    @PatchMapping("/{idResidencia}/baja")
    public ResponseEntity<Void> baja(
                                @PathVariable Long idResidencia) {
        try {
            residenciaService.deleteLogico(idResidencia);
        }catch (ResiException e) {
            // Manejo de excepciones específicas de la aplicación
            throw new ApiException(e, null);
        } catch (Exception e) {
            // Manejo de excepciones genéricas
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }




}

