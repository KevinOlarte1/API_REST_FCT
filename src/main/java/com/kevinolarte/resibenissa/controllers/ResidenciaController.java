package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.services.ResidenciaService;
import com.kevinolarte.resibenissa.services.ResidenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que maneja las operaciones relacionadas con las entidades {@link com.kevinolarte.resibenissa.models.Residencia}.
 * <p>
 * Este controlador permite registrar nuevas residencias y recuperar información sobre residencias existentes.
 * Utiliza los servicios {@link ResidenciaService} y {@link ResidenteService} para gestionar la lógica de negocio.
 * </p>
 *
 * @author Kevin
 */
@RequestMapping("/resi")
@RestController
@AllArgsConstructor
public class ResidenciaController {

    private final ResidenciaService residenciaService;
    private final ResidenteService residenteService;


    /**
     * Registra una nueva residencia en el sistema.
     * <p>
     * Este método recibe los datos de una residencia desde el cuerpo de la petición en formato JSON,
     * valida la información mediante el servicio {@link ResidenciaService} y guarda la entidad si es válida.
     * </p>
     *
     * @param residenciaDto Datos de la residencia a registrar.
     * @return {@link ResponseEntity} con el DTO de respuesta y el estado HTTP 200 si el registro fue exitoso.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de errores de validación o duplicación.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenciaResponseDto> addResidencia(@RequestBody ResidenciaDto residenciaDto) {
            ResidenciaResponseDto residencia = residenciaService.save(residenciaDto);
            return ResponseEntity.ok(residencia);

    }

    /**
     * Obtiene una o varias residencias del sistema.
     * <p>
     * Si se proporciona un parámetro {@code idResidencia}, se devuelve únicamente la residencia con ese ID.
     * Si no se proporciona, se devuelven todas las residencias disponibles.
     * </p>
     *
     * @param idResidencia ID de la residencia específica a buscar (opcional).
     * @return {@link ResponseEntity} con la lista de residencias encontradas.
     */
    @GetMapping()
    public ResponseEntity<List<ResidenciaResponseDto>> getResidencia(
            @RequestParam(required = false) Long idResidencia) {
        List<ResidenciaResponseDto> residencias = residenciaService.getResidencias(idResidencia);
        return ResponseEntity.ok(residencias);

    }

}
