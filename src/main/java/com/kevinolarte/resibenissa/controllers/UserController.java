package com.kevinolarte.resibenissa.controllers;

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

    /**
     * Obtiene una lista de usuarios dentro de una residencia, aplicando filtros opcionales.
     *
     * @param email Filtro por email (opcional).
     * @param enabled Filtro por estado habilitado (opcional).
     * @param idJuego Filtro por ID de juego asociado (opcional).
     * @return {@link ResponseEntity} con la lista de usuarios filtrados.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long idJuego,
            @RequestParam(required = false) Long minRegistro,
            @RequestParam(required = false) Long maxRegistro) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.getAll(currentUser.getResidencia().getId(), email, enabled, idJuego, minRegistro, maxRegistro));

    }

    @GetMapping("/getAll/bajas")
    public ResponseEntity<List<UserResponseDto>> getAllBajas(
            @RequestParam(required = false) String email) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.getAllBajas(currentUser.getResidencia().getId(), email));

    }


    /**
     * Elimina un usuario si pertenece a la residencia y no tiene registros dependientes.
     *
     * @param idUser ID del usuario a eliminar.
     * @return {@link ResponseEntity} con estado 204 No Content si se elimina correctamente.
     */
    @DeleteMapping("/{idUser}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        userService.deleteFisico(currentUser.getResidencia().getId(),idUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Elimina las referencias a registros de juego de un usuario sin eliminarlo.
     *
     * @param idUser ID del usuario.
     * @return {@link ResponseEntity} con estado 204 No Content si se eliminan correctamente las referencias.
     */
    @DeleteMapping("/{idUser}/delete/referencies")
    public ResponseEntity<Void> deleteReferencies(
            @PathVariable Long idUser) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        userService.deleteReferencies(currentUser.getResidencia().getId(), idUser);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
     * @param idUser ID del usuario.
     * @param userDto Datos a actualizar.
     * @return {@link ResponseEntity} con los datos del usuario actualizado.
     */
    @PatchMapping("/{idUser}/update")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long idUser,
            @RequestBody UserDto userDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.update(currentUser.getResidencia().getId(), idUser, userDto));
    }

    /**
     * Cambia la contraseña de un usuario validando la anterior.
     *
     * @param idUser ID del usuario.
     * @param changePasswordUserDto DTO con la contraseña actual y la nueva.
     * @return {@link ResponseEntity} con los datos del usuario tras el cambio.
     */
    @PatchMapping("/{idUser}/update/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(
            @PathVariable Long idUser,
            @RequestBody ChangePasswordUserDto changePasswordUserDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.updatePassword(currentUser.getResidencia().getId(), idUser, changePasswordUserDto));
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

    /*
     * Registra un nuevo usuario a partir de los datos proporcionados en el DTO.
     * <p>
     * Se validan los campos obligatorios, el formato y unicidad del email, y la existencia de la residencia.
     * </p>
     *
     * param userDto DTO con los datos del nuevo usuario.
     * @return {@link ResponseEntity} con los datos del usuario creado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en casi de multiples casos.

    PostMapping("/add")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserDto userDto) {
    UserResponseDto user = userService.save(userDto);
    return ResponseEntity.ok(user);

    } */
}
