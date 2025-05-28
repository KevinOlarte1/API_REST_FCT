package com.kevinolarte.resibenissa.controllers.modulojuego.registroJuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO;
import com.kevinolarte.resibenissa.dto.out.modulojuego.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.enums.Filtrado.RegistroJuegoFiltrado;
import com.kevinolarte.resibenissa.enums.modulojuego.TipoAgrupacion;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.modulojuego.RegistroJuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestionar los registros de juegos jugados por los residentes.
 * <p>
 * Permite agregar nuevos registros, consultar estadísticas con múltiples filtros,
 * actualizar observaciones y eliminar registros específicos.
 * </p>
 * <p>
 * URL base: <code>admin/resi</code>.
 * </p>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi")
@RestController
@AllArgsConstructor
public class RegistroJuegoAdminController {

    private final RegistroJuegoService registroJuegoService;

    /**
     * Crea un nuevo registro de juego para un residente en una residencia y juego específicos.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param registroJuegoDto Datos del registro a guardar.
     * @return El registro de juego creado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("{idResidencia}/registro/add")
    public ResponseEntity<RegistroJuegoResponseDto> add(
            @PathVariable Long idResidencia,
            @RequestParam Long idJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto) {
        try {
            RegistroJuegoResponseDto registroJuego = registroJuegoService.add(idResidencia, idJuego, registroJuegoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registroJuego);
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Obtiene un registro de juego específico mediante su ID.
     *
     * @param idResidencia ID de la residencia.
     * @param idRegistroJuego ID del registro de juego.
     * @return El registro de juego solicitado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/{idResidencia}/registro/{idRegistroJuego}/get")
    public ResponseEntity<RegistroJuegoResponseDto> get(
            @PathVariable Long idResidencia,
            @PathVariable Long idRegistroJuego) {
        try {
            return ResponseEntity.ok(registroJuegoService.get(idResidencia, idRegistroJuego));
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Obtiene una lista de registros de juego filtrados por varios parámetros.
     * @param idJuego ID del juego.
     * @param dificultad Dificultad del juego (opcional).
     * @param edad Edad del residente (opcional).
     * @param minEdad Edad mínima del residente (opcional).
     * @param maxEdad Edad máxima del residente (opcional).
     * @param idResidente ID del residente (opcional).
     * @param fecha Fecha del registro (opcional).
     * @param minFecha Fecha mínima del registro (opcional).
     * @param maxFecha Fecha máxima del registro (opcional).
     * @param promedio true si se desea obtener el promedio de los registros, false en caso contrario.
     * @param masPromedio true si se desea obtener los registros con puntajes superiores al promedio, false en caso contrario.
     * @param menosPromedio true si se desea obtener los registros con puntajes inferiores al promedio, false en caso contrario.
     * @return Lista de registros de juego filtrados.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/registro/getAll")
    public ResponseEntity<List<RegistroJuegoResponseDto>> getAll(
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) Integer minEdad,
            @RequestParam(required = false) Integer maxEdad,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDate minFecha,
            @RequestParam(required = false) LocalDate maxFecha,
            @RequestParam(required = false) boolean promedio,
            @RequestParam(required = false) boolean masPromedio,
            @RequestParam(required = false) boolean menosPromedio,
            @RequestParam(required = false) RegistroJuegoFiltrado filtrado,
            @RequestParam(required = false) Boolean comentado) {
        try {
            return ResponseEntity.ok(registroJuegoService.getAll(
                    idJuego, dificultad, edad, minEdad, maxEdad,
                    idResidente, fecha, minFecha, maxFecha,
                    promedio, masPromedio, menosPromedio,
                    filtrado, comentado));
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Obtiene una lista de registros de juego filtrados por varios parámetros de una residencia especifica
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param dificultad Dificultad del juego (opcional).
     * @param edad Edad del residente (opcional).
     * @param minEdad Edad mínima del residente (opcional).
     * @param maxEdad Edad máxima del residente (opcional).
     * @param idResidente ID del residente (opcional).
     * @param fecha Fecha del registro (opcional).
     * @param minFecha Fecha mínima del registro (opcional).
     * @param maxFecha Fecha máxima del registro (opcional).
     * @param promedio true si se desea obtener el promedio de los registros, false en caso contrario.
     * @param masPromedio true si se desea obtener los registros con puntajes superiores al promedio, false en caso contrario.
     * @param menosPromedio true si se desea obtener los registros con puntajes inferiores al promedio, false en caso contrario.
     * @return Lista de registros de juego filtrados.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/{idResidencia}/registro/getAll")
    public ResponseEntity<List<RegistroJuegoResponseDto>> getAll(
            @PathVariable Long idResidencia,
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) Integer minEdad,
            @RequestParam(required = false) Integer maxEdad,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDate minFecha,
            @RequestParam(required = false) LocalDate maxFecha,
            @RequestParam(required = false) boolean promedio,
            @RequestParam(required = false) boolean masPromedio,
            @RequestParam(required = false) boolean menosPromedio,
            @RequestParam(required = false) RegistroJuegoFiltrado filtrado,
            @RequestParam(required = false) Boolean comentado) {
        try {
            return ResponseEntity.ok(registroJuegoService.getAll(
                    idResidencia, idJuego, dificultad, edad, minEdad, maxEdad,
                    idResidente, fecha, minFecha, maxFecha,
                    promedio, masPromedio, menosPromedio,
                    filtrado, comentado));
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Actualiza un registro de juego existente añadiendo o modificando una observación.
     *
     * @param idResidencia ID de la residencia donde se encuentra el registro.
     * @param idRegistroJuego ID del registro a actualizar.
     * @param registroJuegoDto DTO con los nuevos datos (principalmente observación).
     * @return Registro de juego actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idResidencia}/registro/{idRegistroJuego}/addComment")
    public ResponseEntity<RegistroJuegoResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idRegistroJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto) {
        try {
            return ResponseEntity.ok(registroJuegoService.update(idResidencia, idRegistroJuego, registroJuegoDto));
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    /**
     * Elimina un registro de juego específico.
     *
     * @param idResidencia ID de la residencia donde se encuentra el registro.
     * @param idRegistroJuego ID del registro a eliminar.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @DeleteMapping("/{idResidencia}/registro/{idRegistroJuego}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idRegistroJuego) {
        try {
            registroJuegoService.delete(idResidencia, idRegistroJuego);
            return ResponseEntity.noContent().build();
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    @GetMapping("/{idResidencia}/registro/media-duracion")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaDuracionPorResidencia(
            @PathVariable Long idResidencia,
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad) {


        try{
            List<MediaRegistroDTO> medias = registroJuegoService.getMediaDuracionPorResidencia(idResidencia, tipo, dificultad);
            return ResponseEntity.ok(medias);
        }catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    @GetMapping("/registro/media-duracion")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaDuracionPorResidencia(
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad) {


        try{
            List<MediaRegistroDTO> medias = registroJuegoService.getMediaDuracionGlobal(tipo, dificultad);
            return ResponseEntity.ok(medias);
        }catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    @GetMapping("/{idResidencia}/registro/media-num")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaErroresPorResidencia(
            @PathVariable Long idResidencia,
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad) {


        try {
            List<MediaRegistroDTO> medias = registroJuegoService.getMediaErroresPorResidencia(idResidencia, tipo, dificultad);
            return ResponseEntity.ok(medias);
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    @GetMapping("/registro/media-num")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaErroresPorResidencia(
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad) {


        try {
            List<MediaRegistroDTO> medias = registroJuegoService.getMediaErroresGlobal(tipo, dificultad);
            return ResponseEntity.ok(medias);
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }


}
