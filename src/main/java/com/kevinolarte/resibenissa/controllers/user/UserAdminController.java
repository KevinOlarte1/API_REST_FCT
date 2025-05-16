package com.kevinolarte.resibenissa.controllers.user;

import com.kevinolarte.resibenissa.dto.in.UserDto;
import com.kevinolarte.resibenissa.dto.in.auth.ChangePasswordUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar usuarios en una residencia.
 * <p>
 * Permite registrar nuevos usuarios y obtener información sobre ellos.
 * </p>
 *
 * URL Base: {@code /resi/{idResidencia}/user}
 *
 * @author Kevin Olarte
 */
@RequestMapping("/admin/resi")
@RestController
@AllArgsConstructor
public class UserAdminController {

    private final UserService userService;

    /**
     * Registra un nuevo usuario a partir de los datos proporcionados en el DTO.
     * <p>
     * Se validan los campos obligatorios, el formato y unicidad del email, y la existencia de la residencia.
     * </p>
     *
     * @param idResidencia ID de la residencia a la que pertenece el nuevo usuario.
     * @param userDto DTO con los datos del nuevo usuario.
     * @return {@link ResponseEntity} con los datos del usuario creado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en casi de multiples casos.
     */
    @PostMapping("{idResidencia}/user/add")
    public ResponseEntity<UserResponseDto> addUser(
                                        @PathVariable Long idResidencia,
                                        @RequestBody UserDto userDto) {
        UserResponseDto user = userService.save(idResidencia, userDto);
        return ResponseEntity.ok(user);

    }

    /**
     * Obtiene los datos de un usuario específico dentro de una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idUser ID del usuario a consultar.
     * @return {@link ResponseEntity} con los datos del usuario.
     */
    @GetMapping("{idResidencia}/user/{idUser}/get")
    public ResponseEntity<UserResponseDto> get(
                                        @PathVariable Long idResidencia,
                                        @PathVariable Long idUser) {

        return ResponseEntity.ok(userService.get(idResidencia, idUser));

    }

    /**
     * Obtiene una lista de usuarios dentro de una residencia, aplicando filtros opcionales.
     *
     * @param idResidencia ID de la residencia.
     * @param email Filtro por email (opcional).
     * @param enabled Filtro por estado habilitado (opcional).
     * @param idJuego Filtro por ID de juego asociado (opcional).
     * @return {@link ResponseEntity} con la lista de usuarios filtrados.
     */
    @GetMapping("{idResidencia}/user/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
                                                @PathVariable Long idResidencia,
                                                @RequestParam(required = false) String email,
                                                @RequestParam(required = false) Boolean enabled,
                                                @RequestParam(required = false) Long idJuego,
                                                @RequestParam(required = false) Long minRegistro,
                                                @RequestParam(required = false) Long maxRegistro) {

        return ResponseEntity.ok(userService.getAll(idResidencia, email, enabled, idJuego, minRegistro, maxRegistro));

    }

    /**
     * Obtiene una lista de usuarios dentro de una residencia, aplicando filtros opcionales.
     *
     * @param email Filtro por email (opcional).
     * @param enabled Filtro por estado habilitado (opcional).
     * @param idJuego Filtro por ID de juego asociado (opcional).
     * @return {@link ResponseEntity} con la lista de usuarios filtrados.
     */
    @GetMapping("/user/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
                                                @RequestParam(required = false) String email,
                                                @RequestParam(required = false) Boolean enabled,
                                                @RequestParam(required = false) Long idJuego,
                                                @RequestParam(required = false) Long minRegistro,
                                                @RequestParam(required = false) Long maxRegistro) {

        return ResponseEntity.ok(userService.getAll(email, enabled, idJuego, minRegistro, maxRegistro));

    }

    /**
     * Obtiene una lista de usuarios dados de baja dentro de una residencia, aplicando filtros opcionales.
     *
     * @param idResidencia ID de la residencia.
     * @param email Filtro por email (opcional).
     * @return {@link ResponseEntity} con la lista de usuarios dados de baja.
     */
    @GetMapping("{idResidencia}/user/getAll/bajas")
    public ResponseEntity<List<UserResponseDto>> getAllBajas(
                                                @PathVariable Long idResidencia,
                                                @RequestParam(required = false) String email) {

        return ResponseEntity.ok(userService.getAllBajas(idResidencia, email));

    }

    /**
     * Obtiene una lista de usuarios dados de baja dentro de una residencia, aplicando filtros opcionales.
     *
     * @param email Filtro por email (opcional).
     * @return {@link ResponseEntity} con la lista de usuarios dados de baja.
     */
    @GetMapping("/user/getAll/bajas")
    public ResponseEntity<List<UserResponseDto>> getAllBajas(
                                                @RequestParam(required = false) String email) {

        return ResponseEntity.ok(userService.getAllBajas(email));

    }

    /**
     * Elimina un usuario si pertenece a la residencia y no tiene registros dependientes.
     *
     * @param idResidencia ID de la residencia a la que pertenece el usuario.
     * @param idUser ID del usuario a eliminar.
     * @return {@link ResponseEntity} con estado 204 No Content si se elimina correctamente.
     */
    @DeleteMapping("/{idResidencia}/user/{idUser}/delete")
    public ResponseEntity<Void> delete(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser) {
        userService.deleteFisico(idResidencia,idUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Elimina las referencias a registros de juego de un usuario sin eliminarlo.
     *
     * @param idResidencia ID de la residencia a la que pertenece el usuario.
     * @param idUser ID del usuario.
     * @return {@link ResponseEntity} con estado 204 No Content si se eliminan correctamente las referencias.
     */
    @DeleteMapping("/{idResidencia}/{idUser}/delete/referencies")
    public ResponseEntity<Void> deleteReferencies(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser) {

        userService.deleteReferencies(idResidencia, idUser);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza los datos básicos de un usuario.
     *
     * @param idResidencia ID de la residencia a la que pertenece el usuario.
     * @param idUser ID del usuario.
     * @param userDto Datos a actualizar.
     * @return {@link ResponseEntity} con los datos del usuario actualizado.
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
     * Desactiva un usuario sin eliminarlo físicamente.
     *
     * @param idUser ID del usuario a desactivar.
     * @return {@link ResponseEntity} con estado 204 No Content si se desactiva correctamente.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en caso de error.
     */
    @PatchMapping("/{idResidencia}/user/{idUser}/baja")
    public ResponseEntity<Void> baja(
                                @PathVariable Long idResidencia,
                                @PathVariable Long idUser) {

        userService.deleteLogico(idResidencia, idUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
