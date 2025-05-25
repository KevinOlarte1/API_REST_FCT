package com.kevinolarte.resibenissa.controllers.moduloOrgSalida.evento;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.EventoSalidaResponseDto;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.EventoSalidaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestionar los eventos de salida de una residencia.
 * <p>
 * Permite crear, actualizar, eliminar, consultar y listar eventos de salida
 * asociados a una residencia específica.
 * </p>
 *
 * URL base: /admin/resi
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi")
@RestController
@AllArgsConstructor
public class EventoSalidaAdminController {

    public final EventoSalidaService eventoSalidaService;


    /**
     * Crea un nuevo evento de salida en una residencia.
     *
     * @param idResidencia ID de la residencia donde se creará el evento de salida.
     * @param input DTO que contiene los datos del evento de salida a crear.
     * @return {@link ResponseEntity} con el evento de salida creado.ç
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/{idResidencia}/evento/add")
    public ResponseEntity<EventoSalidaResponseDto> add(
            @PathVariable Long idResidencia,
            @RequestBody EventoSalidaDto input) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(eventoSalidaService.add(input, idResidencia));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }


    /**
     * Obtiene los datos de un evento de salida específico.
     *
     * @param idResidencia ID de la residencia a la que pertenece el evento de salida.
     * @param idEventoSalida ID del evento de salida a consultar.
     * @return {@link ResponseEntity} con los datos del evento de salida encontrado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/{idResidencia}/evento/{idEventoSalida}/get")
    public ResponseEntity<EventoSalidaResponseDto> getEventoSalida(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida) {
        try {
            return ResponseEntity.ok(eventoSalidaService.get(idEventoSalida, idResidencia));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }


    /**
     * Obtiene una lista de eventos de salida con filtros dinámicos.
     *
     * @param idResidencia       ID de la residencia (obligatorio)
     * @param fecha              Fecha exacta del evento
     * @param minFecha           Fecha mínima del evento (rango)
     * @param maxFecha           Fecha máxima del evento (rango)
     * @param estado             Estado del evento (PENDIENTE, REALIZADA, CANCELADA...)
     * @param idResidente        Filtra eventos que contengan al residente con este ID
     * @param idParticipante     Filtra eventos que contengan al participante con este ID
     * @param minRH              Mínimo número de participantes con recursos humanos
     * @param maxRH              Máximo número de participantes con recursos humanos
     * @param minRM              Mínimo número de participantes con recursos materiales
     * @param maxRM              Máximo número de participantes con recursos materiales
     * @return Lista de eventos de salida que cumplen los filtros
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/{idResidencia}/evento/getAll")
    public ResponseEntity<List<EventoSalidaResponseDto>> getAllEventosSalida(
            @PathVariable Long idResidencia,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDate minFecha,
            @RequestParam(required = false) LocalDate maxFecha,
            @RequestParam(required = false) EstadoSalida estado,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Long idParticipante,
            @RequestParam(required = false) Integer minRH,
            @RequestParam(required = false) Integer maxRH,
            @RequestParam(required = false) Integer minRM,
            @RequestParam(required = false) Integer maxRM) {
        try {
            return ResponseEntity.ok(eventoSalidaService.getAll(
                    idResidencia, fecha, minFecha, maxFecha, estado,
                    idResidente, idParticipante, minRH, maxRH, minRM, maxRM));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }

    /**
     * Obtiene una lista de eventos de salida con filtros dinámicos.
     *
     * @param fecha              Fecha exacta del evento
     * @param minFecha           Fecha mínima del evento (rango)
     * @param maxFecha           Fecha máxima del evento (rango)
     * @param estado             Estado del evento (PENDIENTE, REALIZADA, CANCELADA...)
     * @param idResidente        Filtra eventos que contengan al residente con este ID
     * @param idParticipante     Filtra eventos que contengan al participante con este ID
     * @param minRH              Mínimo número de participantes con recursos humanos
     * @param maxRH              Máximo número de participantes con recursos humanos
     * @param minRM              Mínimo número de participantes con recursos materiales
     * @param maxRM              Máximo número de participantes con recursos materiales
     * @return Lista de eventos de salida que cumplen los filtros
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/evento/getAll")
    public ResponseEntity<List<EventoSalidaResponseDto>> getAllEventosSalidaSinResidencia(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDate minFecha,
            @RequestParam(required = false) LocalDate maxFecha,
            @RequestParam(required = false) EstadoSalida estado,
            @RequestParam(required = false) Long idResidente,
            @RequestParam(required = false) Long idParticipante,
            @RequestParam(required = false) Integer minRH,
            @RequestParam(required = false) Integer maxRH,
            @RequestParam(required = false) Integer minRM,
            @RequestParam(required = false) Integer maxRM) {
        try {
            return ResponseEntity.ok(eventoSalidaService.getAll(
                    fecha, minFecha, maxFecha, estado,
                    idResidente, idParticipante, minRH, maxRH, minRM, maxRM));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }




    /**
     * Elimina un evento de salida de una residencia.
     * <p>
     * La eliminación incluye también a todos los participantes asociados al evento.
     * </p>
     *
     * @param idResidencia ID de la residencia a la que pertenece el evento de salida.
     * @param idEventoSalida ID del evento de salida a eliminar.
     * @return {@link ResponseEntity} sin contenido si la eliminación fue exitosa.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @DeleteMapping("/{idResidencia}/evento/{idEventoSalida}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida) {
        try {
            eventoSalidaService.delete(idEventoSalida, idResidencia);
            return ResponseEntity.noContent().build();
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }

    /**
     * Cambia el nombre de un evento de salida.
     *
     * @param idResidencia ID de la residencia a la que pertenece el evento de salida.
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param nombre Nuevo nombre del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idResidencia}/evento/{idEventoSalida}/changeNombre")
    public ResponseEntity<EventoSalidaResponseDto> changeNombre(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestParam String nombre) {
        try {
            return ResponseEntity.ok(eventoSalidaService.changeNombre(idEventoSalida, nombre, idResidencia));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }

    /**
     * Cambia la descripción de un evento de salida.
     *
     * @param idResidencia ID de la residencia a la que pertenece el evento de salida.
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param descripcion Nueva descripción del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idResidencia}/evento/{idEventoSalida}/changeDescripcion")
    public ResponseEntity<EventoSalidaResponseDto> changeDescripcion(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestParam String descripcion) {
        try {
            return ResponseEntity.ok(eventoSalidaService.changeDescripcion(idEventoSalida, descripcion, idResidencia));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }

    /**
     * Cambia la fecha de un evento de salida.
     *
     * @param idResidencia ID de la residencia a la que pertenece el evento de salida.
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param fecha Nueva fecha del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idResidencia}/evento/{idEventoSalida}/changeFecha")
    public ResponseEntity<EventoSalidaResponseDto> changeFecha(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestParam LocalDateTime fecha) {
        try {
            return ResponseEntity.ok(eventoSalidaService.changeFecha(idEventoSalida, fecha, idResidencia));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }

    /**
     * Cambia el estado de un evento de salida.
     *
     * @param idResidencia ID de la residencia a la que pertenece el evento de salida.
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param estado Nuevo estado del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idResidencia}/evento/{idEventoSalida}/changeEstado")
    public ResponseEntity<EventoSalidaResponseDto> changeEstado(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestParam EstadoSalida estado) {
        try {
            return ResponseEntity.ok(eventoSalidaService.changeEstado(idEventoSalida, estado, idResidencia));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }

    /**
     * Actualiza los datos de un evento de salida.
     *
     * @param idResidencia ID de la residencia a la que pertenece el evento de salida.
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param input DTO con los nuevos datos del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idResidencia}/evento/{idEventoSalida}/update")
    public ResponseEntity<EventoSalidaResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idEventoSalida,
            @RequestBody EventoSalidaDto input) {
        try {
            return ResponseEntity.ok(eventoSalidaService.update(input, idResidencia, idEventoSalida));
        } catch (ResiException e) {
            throw new ApiException(e, null);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), null);
        }
    }

}
