package com.kevinolarte.resibenissa.controllers.residencia;

import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaPublicResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
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
     * Obtiene una residencia por su ID.
     *
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el DTO de la residencia encontrada.
     * @throws ResiException si ocurre un error al obtener la residencia.
     */
    @GetMapping("/get")
    public ResponseEntity<ResidenciaResponseDto> get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        ResidenciaResponseDto residencia;
        try{
            residencia = residenciaService.get(currentUser.getResidencia().getId());
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.ok(residencia);
    }

    /**
     * Obtiene todas las residencias.
     *
     * @return {@link ResponseEntity} con estado {@code 200 OK} y una lista de DTOs de residencias.
     * @throws ApiException si ocurre un error al obtener las residencias.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ResidenciaPublicResponseDto>> getAll() {
        List<ResidenciaPublicResponseDto> residencias;
        try{
            residencias = residenciaService.getAll();
        }catch (Exception e){
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
        return ResponseEntity.ok(residencias);
    }








}
