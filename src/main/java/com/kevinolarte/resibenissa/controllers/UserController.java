package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.config.Conf;
import com.kevinolarte.resibenissa.dto.in.UserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


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
@RequestMapping("/resi/users")
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
     * @param userDto DTO con los datos del nuevo usuario.
     * @return {@link ResponseEntity} con los datos del usuario creado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException en casi de multiples casos.

    @PostMapping("/add")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserDto userDto) {
        UserResponseDto user = userService.save(userDto);
        return ResponseEntity.ok(user);

    } */

    /**
     * Recupera una lista de usuarios registrados, con la opción de aplicar filtros.
     * <p>
     * Se pueden aplicar filtros por ID de residencia, estado de habilitación o email.
     * Si no se proporciona ningún filtro, se devuelven todos los usuarios.
     * </p>
     *
     * @param idResidencia (opcional) ID de la residencia asociada.
     * @param enable       (opcional) Estado de habilitación del usuario (true/false).
     * @param email        (opcional) Email del usuario a buscar.
     * @return {@link ResponseEntity} con la lista de usuarios encontrados.
     */
    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @RequestParam(required = false) Long idResidencia,
            @RequestParam(required = false) Boolean enable,
            @RequestParam(required = false) String email) {
        System.out.println("Entra");
        List<UserResponseDto> users = userService.getUsers(idResidencia, enable, email);
        return ResponseEntity.ok(users);

    }

    /**
     * Elimina un usuario del sistema.
     * <p>
     * Este método recibe un ID de usuario como parámetro y solicita al servicio
     * {@link UserService} que elimine la entidad correspondiente, pero de antemano debe haber usado el metodo {@linkplain com.kevinolarte.resibenissa.controllers.UserController#removeReferencias(Long)}.
     * Si la eliminación es exitosa, se devuelve un DTO con los datos del usuario eliminado.
     * </p>
     *
     * @param idUser ID del usuario que se desea eliminar.
     * @return {@link ResponseEntity} que contiene el DTO del usuario eliminado y el estado HTTP 200 (OK).
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si el usuario no existe o tiene aún referencias asociadas..
     */
    @DeleteMapping("/remove")
    public ResponseEntity<UserResponseDto> removeUser(@RequestParam Long idUser) {
        UserResponseDto userTmp = userService.remove(idUser);
        return ResponseEntity.ok(userTmp);
    }

    /**
     * Elimina todas las referencias que apuntan a el ponendolas a nulas.
     * <p>
     * Este método permite eliminar las referencias asociadas a el para luego eliminar el usuario con el metodo {@linkplain  com.kevinolarte.resibenissa.controllers.UserController#removeUser(Long)}
     * aplicando una lógica especial en el servicio {@link UserService} para manejar las referencias antes de eliminar.
     * </p>
     *
     * @param idUser ID del usuario que se desea eliminar junto con sus referencias.
     * @return {@link ResponseEntity} que contiene el DTO del usuario cuyas referenciass ha sido eliminado y el estado HTTP 200 (OK).
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si el usuario no existe.
     */
    @DeleteMapping("/remove/referencies")
    public ResponseEntity<UserResponseDto> removeReferencias(@RequestParam Long idUser) {
        UserResponseDto userTmp = userService.removeReferencias(idUser);
        return  ResponseEntity.ok(userTmp);
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







}
