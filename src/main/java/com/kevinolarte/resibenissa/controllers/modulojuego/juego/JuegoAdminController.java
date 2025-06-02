package com.kevinolarte.resibenissa.controllers.modulojuego.juego;


import com.kevinolarte.resibenissa.dto.in.modulojuego.JuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.JuegoResponseDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.enums.modulojuego.TipoAgrupacion;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.repositories.modulojuego.RegistroJuegoRepository;
import com.kevinolarte.resibenissa.services.modulojuego.JuegoService;
import com.kevinolarte.resibenissa.services.modulojuego.RegistroJuegoService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gestiona las operaciones relacionadas con los juegos en una residencia.
 * <p>
 * Permite registrar, consultar, listar, actualizar y eliminar juegos asociados a una residencia.
 * </p>
 *
 * Ruta base: <b>/admin/resi/juego</b>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi/juego")
@RestController
@AllArgsConstructor
public class JuegoAdminController {
    private final JuegoService juegoService;
    private final RegistroJuegoRepository registroJuegoRepository;
    private final RegistroJuegoService registroJuegoService;

    /**
     * Registra un nuevo juego asociado a una residencia.
     *
     * @param juegoDto Datos del juego a registrar.
     * @return {@link ResponseEntity} con los datos del juego creado y estado 201 CREATED.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/add")
    public ResponseEntity<JuegoResponseDto> add(@RequestBody JuegoDto juegoDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.save(juegoDto));
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Obtiene los datos de un juego específico en una residencia.
     * @param idJuego ID del juego a consultar.
     * @return {@link ResponseEntity} con los datos del juego solicitado y estado 200 OK.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/{idJuego}/get")
    public ResponseEntity<JuegoResponseDto> get(@PathVariable Long idJuego) {
        try {
            return ResponseEntity.ok(juegoService.get(idJuego));
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Obtiene una lista de todos los juegos registrados en una residencia.
     *
     * @param nombreJuego Nombre del juego para filtrar (opcional).
     * @param maxRegistros Indica si se debe limitar la cantidad de registros devueltos (opcional).
     * @return {@link ResponseEntity} con la lista de juegos y estado 200 OK.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String nombreJuego,
            @RequestParam(required = false) Boolean maxRegistros) {
        try {
            return ResponseEntity.ok(juegoService.getAll(nombreJuego, maxRegistros));
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Elimina un juego de una residencia si no tiene referencias dependientes.
     *
     * @param idJuego ID del juego a eliminar.
     * @return {@link ResponseEntity} con estado 204 NO CONTENT si la eliminación es exitosa.
     * @throws ApiException si ocurre un error al procesar la solicitud o si el juego no puede ser eliminado.
     */
    @DeleteMapping("/{idJuego}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long idJuego) {
        try {
            juegoService.delete(idJuego);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO),e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un juego ya registrado en una residencia.
     *
     * @param idJuego ID del juego a actualizar.
     * @param juegoDto Datos nuevos del juego.
     * @return {@link ResponseEntity} con los datos del juego actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud o si el juego no puede ser actualizado.
     */
    @PatchMapping("/{idJuego}/update")
    public ResponseEntity<JuegoResponseDto> update(
            @PathVariable Long idJuego,
            @RequestBody JuegoDto juegoDto) {
        try {
            return ResponseEntity.ok(juegoService.update(idJuego, juegoDto));
        } catch (ResiException e) {
            throw new ApiException(e,e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO),e.getMessage());
        }
    }

}
