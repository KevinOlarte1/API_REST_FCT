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
 * URL base: /resi/evento
 * @author Kevin Olarte
 */
@RequestMapping("/resi/evento")
@RestController
@AllArgsConstructor
public class EventoSalidaController {

    private final EventoSalidaService eventoSalidaService;


    /**
     * Crea un nuevo evento de salida en una residencia.
     *
     * @param input DTO que contiene los datos del evento de salida a crear.
     * @return {@link ResponseEntity} con el evento de salida creado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PostMapping("/add")
    public ResponseEntity<EventoSalidaResponseDto> add(@RequestBody EventoSalidaDto input) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(eventoSalidaService.add(input, currentUser.getResidencia().getId()));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }


    /**
     * Obtiene los datos de un evento de salida específico.
     *
     * @param idEventoSalida ID del evento de salida a consultar.
     * @return {@link ResponseEntity} con los datos del evento de salida encontrado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/{idEventoSalida}/get")
    public ResponseEntity<EventoSalidaResponseDto> getEventoSalida(@PathVariable Long idEventoSalida) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(eventoSalidaService.get(idEventoSalida, currentUser.getResidencia().getId()));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Obtiene una lista de eventos de salida filtrados por varios parámetros.
     *
     * @param fecha Fecha específica del evento de salida.
     * @param minFecha Fecha mínima del evento de salida.
     * @param maxFecha Fecha máxima del evento de salida.
     * @param estado Estado del evento de salida.
     * @param idResidente ID del residente asociado al evento de salida.
     * @param idParticipante ID del participante asociado al evento de salida.
     * @param minRH Mínimo rango horario del evento de salida.
     * @param maxRH Máximo rango horario del evento de salida.
     * @param minRM Mínimo rango de minutos del evento de salida.
     * @param maxRM Máximo rango de minutos del evento de salida.
     * @return {@link ResponseEntity} con la lista de eventos de salida encontrados.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<EventoSalidaResponseDto>> getAllEventosSalida(
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
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(eventoSalidaService.getAll(
                    currentUser.getResidencia().getId(),
                    fecha, minFecha, maxFecha, estado,
                    idResidente, idParticipante, minRH, maxRH, minRM, maxRM));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }




    /**
     * Elimina un evento de salida de una residencia.
     * <p>
     * La eliminación incluye también a todos los participantes asociados al evento.
     * </p>
     *
     * @param idEventoSalida ID del evento de salida a eliminar.
     * @return {@link ResponseEntity} con estado NO_CONTENT si la eliminación fue exitosa.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @DeleteMapping("/{idEventoSalida}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long idEventoSalida) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            eventoSalidaService.delete(idEventoSalida, currentUser.getResidencia().getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }


    /**
     * Cambia el nombre de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param nombre Nuevo nombre del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idEventoSalida}/changeNombre")
    public ResponseEntity<EventoSalidaResponseDto> changeNombre(
            @PathVariable Long idEventoSalida,
            @RequestParam String nombre) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(eventoSalidaService.changeNombre(idEventoSalida, nombre, currentUser.getResidencia().getId()));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Cambia la descripción de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param descripcion Nueva descripción del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idEventoSalida}/changeDescripcion")
    public ResponseEntity<EventoSalidaResponseDto> changeDescripcion(
            @PathVariable Long idEventoSalida,
            @RequestParam String descripcion) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(eventoSalidaService.changeDescripcion(idEventoSalida, descripcion, currentUser.getResidencia().getId()));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Cambia la fecha de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param fecha Nueva fecha del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idEventoSalida}/ChangeFecha")
    public ResponseEntity<EventoSalidaResponseDto> changeFecha(
            @PathVariable Long idEventoSalida,
            @RequestParam LocalDateTime fecha) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(eventoSalidaService.changeFecha(idEventoSalida, fecha, currentUser.getResidencia().getId()));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Cambia el estado de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param estado Nuevo estado del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idEventoSalida}/changeEstado")
    public ResponseEntity<EventoSalidaResponseDto> changeEstado(
            @PathVariable Long idEventoSalida,
            @RequestParam EstadoSalida estado) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(eventoSalidaService.changeEstado(idEventoSalida, estado, currentUser.getResidencia().getId()));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }

    /**
     * Actualiza los datos de un evento de salida.
     *
     * @param idEventoSalida ID del evento de salida a actualizar.
     * @param input DTO con los nuevos datos del evento de salida.
     * @return {@link ResponseEntity} con el evento de salida actualizado.
     * @throws ApiException si ocurre un error al procesar la solicitud.
     */
    @PatchMapping("/{idEventoSalida}/update")
    public ResponseEntity<EventoSalidaResponseDto> update(
            @PathVariable Long idEventoSalida,
            @RequestBody EventoSalidaDto input) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(eventoSalidaService.update(input, currentUser.getResidencia().getId(), idEventoSalida));
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser);
        }
    }




}
