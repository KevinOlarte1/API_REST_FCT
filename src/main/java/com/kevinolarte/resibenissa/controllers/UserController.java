package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.config.Conf;
import com.kevinolarte.resibenissa.dto.in.UserDto;
import com.kevinolarte.resibenissa.dto.in.auth.ChangePasswordUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Permite registrar nuevos usuarios y obtener usuarios filtrando por residencia, estado habilitado o email.
 * Este controlador forma parte del módulo de gestión de residencias.
 * </p>
 *
 * Ruta base: <b>/resi/users</b>
 *
 * @author Kevin Olarte
 */
@RequestMapping("/resi/{idResidencia}/user")
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
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


    @GetMapping("/{idUser}/get")
    public ResponseEntity<UserResponseDto> get(
            @PathVariable Long idResidencia,
            @PathVariable Long idUser) {

        return ResponseEntity.ok(userService.get(idResidencia, idUser));

    }

    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDto>> getAll(
            @PathVariable Long idResidencia,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long idJuego) {

        return ResponseEntity.ok(userService.getAll(idResidencia, email, enabled, idJuego));

    }


    @DeleteMapping("/{idUser}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long idResidencia,
            @PathVariable Long idUser) {
        userService.delete(idResidencia,idUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/{idUser}/delete/referencies")
    public ResponseEntity<Void> deleteReferencies(
            @PathVariable Long idResidencia,
            @PathVariable Long idUser) {
        userService.deleteReferencies(idResidencia, idUser);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Descarga la imagen por defecto del sistema como un archivo adjunto.
     * <p>
     * Este endpoint devuelve un archivo binario (imagen) con tipo de contenido {@code application/octet-stream},
     * permitiendo al navegador descargarlo como si fuera un archivo externo.
     * <br>
     * Aunque el método recibe un {@code filename} como parámetro, internamente siempre se carga la imagen
     * por defecto definida en {@link com.kevinolarte.resibenissa.config.Conf#imageDefault}.
     * </p>
     * @return {@link ResponseEntity} con el recurso de imagen como archivo descargable.
     */
    @GetMapping("/defualtImage")
    public ResponseEntity<Resource> downloadImage(){
        Resource resource = userService.getImage(Conf.imageDefault);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PatchMapping("/{idUser}/update")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long idResidencia,
            @PathVariable Long idUser,
            @RequestBody UserDto userDto) {

        return ResponseEntity.ok(userService.update(idResidencia, idUser, userDto));
    }

    @PatchMapping("/{idUser}/update/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(
            @PathVariable Long idResidencia,
            @PathVariable Long idUser,
            @RequestBody ChangePasswordUserDto changePasswordUserDto) {

        return ResponseEntity.ok(userService.updatePassword(idResidencia, idUser, changePasswordUserDto));
    }







}
