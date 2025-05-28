package com.kevinolarte.resibenissa.controllers.modulojuego.juego;

import com.kevinolarte.resibenissa.dto.out.modulojuego.JuegoResponseDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.enums.modulojuego.TipoAgrupacion;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.modulojuego.JuegoService;
import com.kevinolarte.resibenissa.services.modulojuego.RegistroJuegoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controlador REST que gestiona las operaciones relacionadas con los juegos en una residencia.
 * <p>
 * Permite registrar, consultar, listar, actualizar y eliminar juegos asociados a una residencia.
 * </p>
 *
 * Ruta base: <b>/resi/juego</b>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/juego")
@RestController
@AllArgsConstructor
public class JuegoController {
    private final JuegoService juegoService;
    private final RegistroJuegoService registroJuegoService;


    /**
     * Obtiene los datos de un juego específico dentro de una residencia.
     *
     * @param idJuego ID del juego a consultar.
     * @return {@link ResponseEntity} con los datos del juego solicitado.
     * @throws ResiException si ocurre un error al obtener el juego.
     */
    @GetMapping("/{idJuego}/get")
    public ResponseEntity<JuegoResponseDto> get(
                                        @PathVariable Long idJuego) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JuegoResponseDto juego;
        try {
            juego = juegoService.get(idJuego);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.ok(juego);
    }

    /**
     * Obtiene una lista de juegos de una residencia, con filtros opcionales por nombre y cantidad máxima.
     *
     * @param nombreJuego Filtro por nombre del juego (opcional).
     * @param maxRegistros Si es {@code true}, limita los resultados a un número máximo (definido en servicio).
     * @return {@link ResponseEntity} con la lista de juegos.
     * @throws ApiException si ocurre un error al obtener los juegos.g
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<JuegoResponseDto>> getAll(
                                        @RequestParam(required = false) String nombreJuego,
                                        @RequestParam(required = false) Boolean maxRegistros) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<JuegoResponseDto> juegos;
        try {
            juegos = juegoService.getAll(nombreJuego, maxRegistros);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.ok(juegos);
    }

    /**
     * Obtiene la media de duración para un juego dentro de una residencia, agrupada por día, mes o año.
     *
     * @param idJuego      ID del juego a analizar.
     * @param tipo         Tipo de agrupación temporal: DIARIO, MENSUAL o ANUAL.
     * @param dificultad   Nivel de dificultad a filtrar (opcional).
     * @return Lista con duración media agrupada por tipo indicado.
     */
    @GetMapping("/{idJuego}/media-duracion")
    public ResponseEntity<List< MediaRegistroDTO>> getMediaDuracionPorJuegoYResidencia(@PathVariable Long idJuego,

                                                                                      @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
                                                                                      @RequestParam(required = false) Dificultad dificultad) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            List<MediaRegistroDTO> resultado = registroJuegoService.getMediaDuracionPorJuegoYResidencia(idJuego, currentUser.getResidencia().getId(), tipo, dificultad);
            return ResponseEntity.ok(resultado);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    /**
     * Obtiene la media de errores para un juego dentro de una residencia, agrupada por día, mes o año.
     *
     * @param idJuego      ID del juego a analizar.
     * @param tipo         Tipo de agrupación temporal: DIARIO, MENSUAL o ANUAL.
     * @param dificultad   Nivel de dificultad a filtrar (opcional).
     * @return Lista con errores medios agrupados por tipo indicado.
     */
    @GetMapping("/{idJuego}/media-errores")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaErroresPorJuegoYResidencia(@PathVariable Long idJuego,
                                                                                     @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
                                                                                     @RequestParam(required = false) Dificultad dificultad) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            List<MediaRegistroDTO> resultado = registroJuegoService.getMediaErroresPorJuegoYResidencia(idJuego, currentUser.getResidencia().getId(), tipo, dificultad);
            return ResponseEntity.ok(resultado);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }










}
