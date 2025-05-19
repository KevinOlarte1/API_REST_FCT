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
 * Controlador REST que gestiona las operaciones relacionadas con los usuarios del sistema.
 * <p>
 * Permite registrar, consultar, actualizar, eliminar y descargar información de usuarios
 * dentro del contexto de una residencia específica.
 * </p>
 *
 * Ruta base: <b>/resi/user</b>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/user")
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(new UserResponseDto(currentUser));
    }
    /**
     * Obtiene los datos de un usuario específico dentro de una residencia.
     *
     * @param idUser ID del usuario a consultar.
     * @return {@link ResponseEntity} con los datos del usuario.
     */
    @GetMapping("/{idUser}/get")
    public ResponseEntity<UserResponseDto> get(
                                        @PathVariable Long idUser) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.get(currentUser.getResidencia().getId(), idUser));

    }

    @GetMapping("/get")
    public ResponseEntity<UserResponseDto> get(
                                        @RequestParam (required = true) String email){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.get(currentUser.getResidencia().getId(), email));

    }

    /**
     * Obtiene una lista de usuarios dentro de una residencia, aplicando filtros opcionales.
     *
     * @param enabled Filtro por estado habilitado (opcional).
     * @param idJuego Filtro por ID de juego asociado (opcional).
     * @return {@link ResponseEntity} con la lista de usuarios filtrados.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
                                               @RequestParam(required = false) Boolean enabled,
                                               @RequestParam(required = false) Long idJuego) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.getAll(currentUser.getResidencia().getId(), enabled, idJuego));

    }

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
     * Descarga la imagen por defecto del sistema como un archivo adjunto.
     * <p>
     * Aunque el endpoint podría aceptar un nombre de archivo, internamente siempre devuelve
     * la imagen definida como {@link Conf#imageDefault}.
     * </p>
     *
     * @return {@link ResponseEntity} con el recurso de imagen descargable.
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
     * Actualiza los datos básicos de un usuario.
     *
     * @param userDto Datos a actualizar.
     * @return {@link ResponseEntity} con los datos del usuario actualizado.
     */
    @PatchMapping("/update")
    public ResponseEntity<UserResponseDto> update(
                                        @RequestBody UserDto userDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.update(currentUser.getResidencia().getId(), currentUser.getId(), userDto));
    }

    /**
     * Cambia la contraseña de un usuario validando la anterior.
     *
     * @param changePasswordUserDto DTO con la contraseña actual y la nueva.
     * @return {@link ResponseEntity} con los datos del usuario tras el cambio.
     */
    @PatchMapping("/update/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(
                                        @RequestBody ChangePasswordUserDto changePasswordUserDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.updatePassword(currentUser.getResidencia().getId(), currentUser.getId(), changePasswordUserDto));
    }


    /**
     * Desactiva un usuario sin eliminarlo físicamente.
     *
     * @param idUser ID del usuario a desactivar.
     * @return {@link ResponseEntity} con estado 204 No Content si se desactiva correctamente.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error.
     */
    @PatchMapping("/{idUser}/baja")
    public ResponseEntity<Void> baja(
                                @PathVariable Long idUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        userService.deleteLogico(currentUser.getResidencia().getId(), idUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }




}
