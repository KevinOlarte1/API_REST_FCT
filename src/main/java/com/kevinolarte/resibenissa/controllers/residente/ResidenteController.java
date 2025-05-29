package com.kevinolarte.resibenissa.controllers.residente;

import com.kevinolarte.resibenissa.config.Conf;
import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.in.moduloReporting.EmailRequestDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO;
import com.kevinolarte.resibenissa.enums.Filtrado.ResidenteFiltrado;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.enums.modulojuego.TipoAgrupacion;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.ResidenteService;
import com.kevinolarte.resibenissa.services.modulojuego.RegistroJuegoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


/**
 * Controlador REST que gestiona las operaciones relacionadas con los residentes de una residencia.
 * <p>
 * Permite registrar, consultar, actualizar, listar y dar de baja residentes.
 * Todas las operaciones se contextualizan dentro de la residencia del usuario autenticado.
 * </p>
 *
 * URL base: {@code /resi/resident}
 *
 * Autor: Kevin Olarte
 */
@RequestMapping("/resi/resident")
@RestController
@AllArgsConstructor
public class ResidenteController {

    private final ResidenteService residenteService;
    private final RegistroJuegoService registroJuegoService;


    /**
     * Registra un nuevo residente en la residencia del usuario autenticado.
     *
     * @param residenteDto DTO con los datos del nuevo residente.
     * @return {@link ResponseEntity} con estado {@code 201 Created} y el residente creado.
     * @throws ApiException si ocurre un error durante el proceso de registro.
     */
    @PostMapping("/add")
    public ResponseEntity<ResidenteResponseDto> add(
                                @RequestBody ResidenteDto residenteDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        ResidenteResponseDto residenteResponseDto;
        try {
            residenteResponseDto = residenteService.add(currentUser.getResidencia().getId(), residenteDto);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(residenteResponseDto);

    }


    /**
     * Obtiene los detalles de un residente específico perteneciente a la misma residencia que el usuario autenticado.
     *
     * @param idResidente ID del residente.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el residente encontrado.
     * @throws ApiException si ocurre un error al intentar obtener el residente.
     */
    @GetMapping("/{idResidente}/get")
    public ResponseEntity<ResidenteResponseDto> get(
                                @PathVariable Long idResidente) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();

        ResidenteResponseDto residenteResponseDto;
        try {
            residenteResponseDto = residenteService.get(currentUser.getResidencia().getId(), idResidente);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.ok(residenteResponseDto);

    }


    /**
     * Lista todos los residentes de la residencia del usuario autenticado, con múltiples filtros opcionales.
     *
     * @param fechaNacimiento Fecha exacta de nacimiento.
     * @param minFNac Fecha mínima de nacimiento.
     * @param maxFNac Fecha máxima de nacimiento.
     * @param maxAge Edad máxima.
     * @param minAge Edad mínima.
     * @param idJuego ID de juego asociado (opcional).
     * @param idEvento ID de evento asociado (opcional).
     * @return {@link ResponseEntity} con la lista de residentes filtrados.
     * @throws ApiException si ocurre un error al intentar obtener los residentes.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ResidenteResponseDto>> getAll(
                                @RequestParam(required = false) LocalDate fechaNacimiento,
                                @RequestParam(required = false) LocalDate minFNac,
                                @RequestParam(required = false) LocalDate maxFNac,
                                @RequestParam(required = false) Integer maxAge,
                                @RequestParam(required = false) Integer minAge,
                                @RequestParam(required = false) Long idJuego,
                                @RequestParam(required = false) Long idEvento,
                                @RequestParam(required = false)ResidenteFiltrado filtrado) {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<ResidenteResponseDto> residentes;
        try{
            residentes = residenteService.getAll(currentUser.getResidencia().getId(),fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento, filtrado);
        }catch (ResiException e){
            throw new ApiException(e, currentUser);
        }catch (Exception e){
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.ok(residentes);
    }


    /**
     * Lista todos los residentes dados de baja en la residencia del usuario autenticado,
     * con posibilidad de filtrar por fechas.
     *
     * @param fecha Fecha exacta de baja (opcional).
     * @param minFecha Fecha mínima de baja (opcional).
     * @param maxFecha Fecha máxima de baja (opcional).
     * @return {@link ResponseEntity} con la lista de residentes dados de baja.
     * @throws ApiException si ocurre un error al intentar obtener los residentes dados de baja.
     */
    @GetMapping("/getAll/bajas")
    public ResponseEntity<List<ResidenteResponseDto>> getAllBajas(
                                @RequestParam(required = false) LocalDate fecha,
                                @RequestParam(required = false) LocalDate minFecha,
                                @RequestParam(required = false) LocalDate maxFecha){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        List<ResidenteResponseDto> residentesBajas;
        try {
            residentesBajas =  residenteService.getAllBajas(currentUser.getResidencia().getId(),fecha, minFecha, maxFecha);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.ok(residentesBajas);
    }

    /**
     * Marca lógicamente como dado de baja a un residente (no lo elimina físicamente).
     *
     * @param idResidente ID del residente a dar de baja.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si se realizó correctamente.
     * @throws ApiException si ocurre un error al intentar dar de baja al residente.
     */
    @PatchMapping("/{idResidente}/baja")
    public ResponseEntity<Void> deleteLogico(
                                @PathVariable Long idResidente) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();
        try{
            residenteService.deleteLogico(currentUser.getResidencia().getId(),idResidente);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Actualiza parcialmente los datos de un residente específico.
     *
     * @param idResidente ID del residente a actualizar.
     * @param residenteDto DTO con los nuevos datos del residente.
     * @return {@link ResponseEntity} con estado {@code 200 OK} y el residente actualizado.
     * @throws ApiException si ocurre un error al intentar actualizar el residente.
     */
    @PatchMapping("/{idResidente}/update")
    public ResponseEntity<ResidenteResponseDto> update(
                                @PathVariable Long idResidente,
                                @RequestBody ResidenteDto residenteDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();
        ResidenteResponseDto residenteResponseDto;
        try {
            residenteResponseDto = residenteService.update(currentUser.getResidencia().getId(), idResidente, residenteDto);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO),currentUser, e.getMessage());
        }
        return ResponseEntity.ok(residenteResponseDto);
    }


    /**
     * Envía un correo electrónico a un familiar de un residente específico.
     *
     * @param idResidente ID del residente.
     * @param emailRequestDto DTO con los detalles del correo electrónico.
     * @return {@link ResponseEntity} con estado {@code 204 No Content} si se envió correctamente.
     */
    @PostMapping("/{idResidente}/sendEmailFamily")
    public ResponseEntity<Void> sendEmailFamily(
                                @PathVariable Long idResidente,
                                @RequestBody EmailRequestDto emailRequestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();
        try{
            residenteService.sendEmailFamiliar(currentUser.getResidencia().getId(), idResidente, emailRequestDto);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{idResidente}/media-duracion")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaDuracion(
            @PathVariable Long idResidente,
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<MediaRegistroDTO> medias;
        try {
            medias = registroJuegoService.getMediaDuracion(currentUser.getResidencia().getId(), idResidente, tipo, dificultad);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }

        return ResponseEntity.ok(medias);
    }

    @GetMapping("/{idResidente}/media-errores")
    public ResponseEntity<List<MediaRegistroDTO>> getMediaErrores(
            @PathVariable Long idResidente,
            @RequestParam(required = false, defaultValue = "DIARIO") TipoAgrupacion tipo,
            @RequestParam(required = false) Dificultad dificultad) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<MediaRegistroDTO> medias;
        try {
            medias = registroJuegoService.getMediaErrores(currentUser.getResidencia().getId(), idResidente, tipo, dificultad);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }

        return ResponseEntity.ok(medias);
    }

    /**
     * Descarga la imagen por defecto del usuario.
     *
     * @return Recurso de imagen por defecto como archivo adjunto.
     */
    @GetMapping("/defualtImage")
    public ResponseEntity<Resource> downloadImage(){
        Resource resource = residenteService.getImage(Conf.imageDefault);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }




}
