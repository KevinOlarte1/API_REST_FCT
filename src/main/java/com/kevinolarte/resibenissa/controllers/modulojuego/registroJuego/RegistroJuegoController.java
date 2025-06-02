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
 * Todas las rutas expuestas están bajo el prefijo <code>/resi/registro</code>.
 * </p>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/registro")
@RestController
@AllArgsConstructor
public class RegistroJuegoController {

    private final RegistroJuegoService registroJuegoService;



    /**
     * Crea un nuevo registro de juego para un residente en una residencia y juego específicos.
     *
     * @param idJuego ID del juego.
     * @param registroJuegoDto Datos del registro a guardar.
     * @return El registro de juego creado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/juego/{idJuego}/add")
    public ResponseEntity<RegistroJuegoResponseDto> add(
            @PathVariable Long idJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            RegistroJuegoResponseDto registroJuego = registroJuegoService.add(currentUser.getResidencia().getId(), idJuego, registroJuegoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registroJuego);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    /**
     * Obtiene un registro de juego específico mediante su ID.
     *
     *@param idRegistroJuego ID del registro de juego.
     * @return El registro de juego solicitado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/{idRegistroJuego}/get")
    public ResponseEntity<RegistroJuegoResponseDto> get(@PathVariable Long idRegistroJuego) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(registroJuegoService.get(currentUser.getResidencia().getId(), idRegistroJuego));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    /**
     * Obtiene una lista de registros de juego filtrados por varios parámetros.
     * @param idJuego ID del juego.
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
     * @param dificultad Dificultad del juego (opcional).
     * @return Lista de registros de juego filtrados.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<RegistroJuegoResponseDto>> getAll(
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) Integer minEdad,
            @RequestParam(required = false) Integer maxEdad,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDate minFecha,
            @RequestParam(required = false) LocalDate maxFecha,
            @RequestParam(required = false) boolean promedio,
            @RequestParam(required = false) boolean masPromedio,
            @RequestParam(required = false) boolean menosPromedio,
            @RequestParam(required = false) RegistroJuegoFiltrado filtrado,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Boolean comentado) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(
                    registroJuegoService.getAll(
                            currentUser.getResidencia().getId(),
                            idJuego, dificultad, edad, minEdad, maxEdad,
                            idResidente, fecha, minFecha, maxFecha,
                            promedio, masPromedio, menosPromedio, filtrado, comentado
                    )
            );
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }



    /**
     * Actualiza un registro de juego existente añadiendo o modificando una observación.
     *
     * @param idRegistroJuego ID del registro a actualizar.
     * @param registroJuegoDto DTO con los nuevos datos (principalmente observación).
     * @return Registro de juego actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idRegistroJuego}/addComment")
    public ResponseEntity<RegistroJuegoResponseDto> update(
            @PathVariable Long idRegistroJuego,
            @RequestBody RegistroJuegoDto registroJuegoDto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(registroJuegoService.update(currentUser.getResidencia().getId(), idRegistroJuego, registroJuegoDto));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }


    /**
     * Elimina un registro de juego específico.
     *
     * @param idRegistroJuego ID del registro a eliminar.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @DeleteMapping("/{idRegistroJuego}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long idRegistroJuego) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            registroJuegoService.delete(currentUser.getResidencia().getId(), idRegistroJuego);
            return ResponseEntity.noContent().build();
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    @GetMapping("/media-duracion")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaDuracionPorResidencia(
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Long idJuego) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        try{
            List<MediaRegistroDTO> medias = registroJuegoService.getMediaDuracionPorResidencia(currentUser.getResidencia().getId(), tipo, dificultad, idJuego);
            return ResponseEntity.ok(medias);
        }catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    @GetMapping("/media-num")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaErroresPorResidencia(
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Long idJuego) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        try {
            List<MediaRegistroDTO> medias = registroJuegoService.getMediaErroresPorResidencia(currentUser.getResidencia().getId(), tipo, dificultad, idJuego);
            return ResponseEntity.ok(medias);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }


    @GetMapping("/residente/{idResidente}/media-duracion")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaDuracion(
            @PathVariable Long idResidente,
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Long idJuego) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<MediaRegistroDTO> medias;
        try {
            medias = registroJuegoService.getMediaDuracion(currentUser.getResidencia().getId(), idResidente, tipo, dificultad, idJuego);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }

        return ResponseEntity.ok(medias);
    }

    @GetMapping("/residente/{idResidente}/media-num")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaErrores(
            @PathVariable Long idResidente,
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Long idJuego) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<MediaRegistroDTO> medias;
        try {
            medias = registroJuegoService.getMediaErrores(currentUser.getResidencia().getId(), idResidente, tipo, dificultad, idJuego);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }

        return ResponseEntity.ok(medias);
    }






}
