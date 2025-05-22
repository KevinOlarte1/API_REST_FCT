package com.kevinolarte.resibenissa.controllers.user;

import com.kevinolarte.resibenissa.dto.in.UserDto;
import com.kevinolarte.resibenissa.dto.in.auth.ChangePasswordUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la administración de usuarios dentro de una residencia.
 *
 * Proporciona operaciones para registrar, consultar, actualizar, desactivar
 * y eliminar usuarios. Admite filtros por estado, juego asociado y fecha de baja.
 *
 * URL base: {@code /admin/resi}
 *
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi")
@RestController
@AllArgsConstructor
public class UserAdminController {

    private final UserService userService;


    /**
     * Registra un nuevo usuario en una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param userDto Datos del usuario a registrar.
     * @return Usuario creado.
     */
    @PostMapping("{idResidencia}/user/add")
    public ResponseEntity<UserResponseDto> addUser(
                                @PathVariable Long idResidencia,
                                @RequestBody UserDto userDto) {

        UserResponseDto user = userService.add(idResidencia, userDto);
        return ResponseEntity.ok(user);

    }

    /**
     * Obtiene un usuario por su ID dentro de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idUser ID del usuario.
     * @return Datos del usuario.
     */
    @GetMapping("{idResidencia}/user/{idUser}/get")
    public ResponseEntity<UserResponseDto> get(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser) {

        return ResponseEntity.ok(userService.get(idResidencia, idUser));

    }

    /**
     * Obtiene un usuario por su email dentro de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param email Email del usuario.
     * @return Datos del usuario.
     */
    @GetMapping("/{idResidencia}/user/get")
    public ResponseEntity<UserResponseDto> get(
                                @PathVariable Long idResidencia,
                                @RequestParam(required = true) String email) {

        return ResponseEntity.ok(userService.get(idResidencia, email));

    }


    /**
     * Obtiene una lista de usuarios filtrada por estado y/o juego dentro de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param enabled Filtro por estado habilitado (opcional).
     * @param idJuego Filtro por ID de juego (opcional).
     * @return Lista de usuarios.
     */
    @GetMapping("{idResidencia}/user/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
                                @PathVariable Long idResidencia,
                                @RequestParam(required = false) Boolean enabled,
                                @RequestParam(required = false) Long idJuego) {

        return ResponseEntity.ok(userService.getAll(idResidencia, enabled, idJuego));

    }

    /**
     * Obtiene todos los usuarios, con filtros por estado y/o juego (sin filtrar por residencia).
     *
     * @param enabled Filtro por estado habilitado (opcional).
     * @param idJuego Filtro por ID de juego (opcional).
     * @return Lista de usuarios.
     */
    @GetMapping("/user/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
                                @RequestParam(required = false) Boolean enabled,
                                @RequestParam(required = false) Long idJuego) {

        return ResponseEntity.ok(userService.getAll(enabled, idJuego));

    }

    /**
     * Obtiene una lista de usuarios dados de baja en una residencia, con filtros de fecha.
     *
     * @param idResidencia ID de la residencia.
     * @param fecha Fecha exacta de baja (opcional).
     * @param minFecha Fecha mínima de baja (opcional).
     * @param maxFecha Fecha máxima de baja (opcional).
     * @return Lista de usuarios dados de baja.
     */
    @GetMapping("{idResidencia}/user/getAll/bajas")
    public ResponseEntity<List<UserResponseDto>> getAllBajas(
                                @PathVariable Long idResidencia,
                                @RequestParam(required = false)LocalDate fecha,
                                @RequestParam(required = false) LocalDate minFecha,
                                @RequestParam(required = false) LocalDate maxFecha){

        return ResponseEntity.ok(userService.getAllBajas(idResidencia, fecha, minFecha, maxFecha));

    }

    /**
     * Obtiene todos los usuarios dados de baja (sin filtrar por residencia).
     *
     * @param fecha Fecha exacta de baja (opcional).
     * @param minFecha Fecha mínima de baja (opcional).
     * @param maxFecha Fecha máxima de baja (opcional).
     * @return Lista de usuarios dados de baja.
     */
    @GetMapping("/user/getAll/bajas")
    public ResponseEntity<List<UserResponseDto>> getAllBajas(
                                @RequestParam(required = false)LocalDate fecha,
                                @RequestParam(required = false) LocalDate minFecha,
                                @RequestParam(required = false) LocalDate maxFecha){

        return ResponseEntity.ok(userService.getAllBajas(fecha, minFecha, maxFecha));

    }

    /**
     * Elimina físicamente un usuario de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idUser ID del usuario.
     * @return HTTP 204 si se elimina correctamente.
     */
    @DeleteMapping("/{idResidencia}/user/{idUser}/delete")
    public ResponseEntity<Void> delete(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser) {
        userService.deleteFisico(idResidencia,idUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Elimina las referencias del usuario a registros dependientes (sin eliminar el usuario).
     *
     * @param idResidencia ID de la residencia.
     * @param idUser ID del usuario.
     * @return HTTP 204 si se eliminan correctamente las referencias.
     */
    @DeleteMapping("/{idResidencia}/{idUser}/delete/referencies")
    public ResponseEntity<Void> deleteReferencies(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser) {

        userService.deleteReferencies(idResidencia, idUser);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza los datos de un usuario.
     *
     * @param idResidencia ID de la residencia.
     * @param idUser ID del usuario.
     * @param userDto Nuevos datos del usuario.
     * @return Usuario actualizado.
     */
    @PatchMapping("/{idResidencia}/user/{idUser}/update")
    public ResponseEntity<UserResponseDto> update(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser,
                                @RequestBody UserDto userDto) {

        return ResponseEntity.ok(userService.update(idResidencia, idUser, userDto));
    }

    /**
     * Cambia la contraseña de un usuario validando la anterior.
     *
     * @param idResidencia ID de la residencia a la que pertenece el usuario.
     * @param idUser ID del usuario.
     * @param changePasswordUserDto DTO con la contraseña actual y la nueva.
     * @return {@link ResponseEntity} con los datos del usuario tras el cambio.
     */
    @PatchMapping("/{idResidencia}/user/{idUser}/update/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser,
                                @RequestBody ChangePasswordUserDto changePasswordUserDto) {

        return ResponseEntity.ok(userService.updatePassword(idResidencia, idUser, changePasswordUserDto));
    }


    /**
     * Marca un usuario como dado de baja (borrado lógico).
     *
     * @param idResidencia ID de la residencia.
     * @param idUser ID del usuario.
     * @return HTTP 204 si se desactiva correctamente.
     */
    @PatchMapping("/{idResidencia}/user/{idUser}/baja")
    public ResponseEntity<Void> baja(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser) {

        userService.deleteLogico(idResidencia, idUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
