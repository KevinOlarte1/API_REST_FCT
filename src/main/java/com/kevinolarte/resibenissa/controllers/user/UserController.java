package com.kevinolarte.resibenissa.controllers.user;

import com.kevinolarte.resibenissa.config.Conf;
import com.kevinolarte.resibenissa.dto.in.UserDto;
import com.kevinolarte.resibenissa.dto.in.auth.ChangePasswordUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los usuarios.
 * Expone endpoints para obtener, actualizar y dar de baja usuarios dentro de una residencia.
 * <p>
 * URL Base: {@code /resi/user}
 * @author Kevin Olarte
 */
@RequestMapping("/resi/user")
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Obtiene la información del usuario actualmente autenticado.
     *
     * @return DTO con los datos del usuario autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(new UserResponseDto(currentUser));
    }

    /**
     * Obtiene la información de un usuario específico por su ID, perteneciente a la misma residencia del usuario autenticado.
     *
     * @param idUser ID del usuario a consultar.
     * @return DTO con los datos del usuario.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error o problemas
     */
    @GetMapping("/{idUser}/get")
    public ResponseEntity<UserResponseDto> get(
                                @PathVariable Long idUser) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.get(currentUser.getResidencia().getId(), idUser));

    }

    /**
     * Obtiene la información de un usuario específico por su email, dentro de la misma residencia del usuario autenticado.
     *
     * @param email Email del usuario a consultar.
     * @return DTO con los datos del usuario.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error o problemas
     */
    @GetMapping("/get")
    public ResponseEntity<UserResponseDto> get(
                                @RequestParam (required = true) String email){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.get(currentUser.getResidencia().getId(), email));

    }

    /**
     * Obtiene todos los usuarios filtrando opcionalmente por si están habilitados y
     * por ID de juego que han registrado partidas.
     *
     * @param enabled Filtro opcional para usuarios habilitados.
     * @param idJuego Filtro opcional por ID de juego.
     * @return Lista de DTOs con los datos de los usuarios.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error o problemas
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
                               @RequestParam(required = false) Boolean enabled,
                               @RequestParam(required = false) Long idJuego) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.getAll(currentUser.getResidencia().getId(), enabled, idJuego));

    }

    /**
     * Obtiene todos los usuarios dados de baja, filtrando opcionalmente por fechas.
     *
     * @param fecha     Fecha exacta de baja.
     * @param minFecha  Fecha mínima de baja.
     * @param maxFecha  Fecha máxima de baja.
     * @return Lista de usuarios dados de baja en el rango indicado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error o problemas
     */
    @GetMapping("/getAll/bajas")
    public ResponseEntity<List<UserResponseDto>> getAllBajas(
                                @RequestParam(required = false) LocalDate fecha,
                                @RequestParam(required = false) LocalDate minFecha,
                                @RequestParam(required = false) LocalDate maxFecha){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.getAllBajas(currentUser.getResidencia().getId(), fecha, minFecha, maxFecha));

    }

    /**
     * Descarga la imagen por defecto del usuario.
     *
     * @return Recurso de imagen por defecto como archivo adjunto.
     */
    @GetMapping("/defualtImage")
    public ResponseEntity<Resource> downloadImage(){
        Resource resource = userService.getImage(Conf.imageDefault);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Actualiza los datos personales del usuario autenticado.
     *
     * @param userDto Objeto DTO con los nuevos datos del usuario.
     * @return DTO actualizado con la información del usuario.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error o problemas
     */
    @PatchMapping("/update")
    public ResponseEntity<UserResponseDto> update(
                                @RequestBody UserDto userDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.update(currentUser.getResidencia().getId(), currentUser.getId(), userDto));
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param changePasswordUserDto DTO con la contraseña antigua y la nueva.
     * @return DTO actualizado del usuario tras el cambio de contraseña.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error o problemas
     */
    @PatchMapping("/update/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(
                                @RequestBody ChangePasswordUserDto changePasswordUserDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.updatePassword(currentUser.getResidencia().getId(), currentUser.getId(), changePasswordUserDto));
    }

    /**
     * Marca lógicamente como dado de baja al usuario.
     *
     * @return Respuesta sin contenido (HTTP 204) si se realiza correctamente.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error o problemas
     */
    @PatchMapping("/baja")
    public ResponseEntity<Void> baja() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        userService.deleteLogico(currentUser.getResidencia().getId(), currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }




}
