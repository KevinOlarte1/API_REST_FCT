package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.config.Conf;
import com.kevinolarte.resibenissa.dto.in.UserDto;
import com.kevinolarte.resibenissa.dto.in.auth.ChangePasswordUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Servicio que gestiona la lógica relacionada con los usuarios del sistema.
 * <p>
 * Permite registrar nuevos usuarios con validaciones y filtros para consultas.
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ResidenciaService residenciaService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema a partir de los datos proporcionados.
     * <p>
     * Este método realiza las siguientes validaciones antes de persistir al usuario:
     * <ul>
     *   <li>Todos los campos obligatorios (nombre, apellido, email, contraseña, residencia) deben estar presentes y no vacíos.</li>
     *   <li>El formato del correo electrónico debe ser válido.</li>
     *   <li>El correo electrónico no debe estar ya registrado.</li>
     *   <li>La residencia asociada debe existir en la base de datos.</li>
     * </ul>
     * Si alguna de estas validaciones falla, se lanza una excepción {@link com.kevinolarte.resibenissa.exceptions.ApiException}
     * con un código de error correspondiente del enum {@link com.kevinolarte.resibenissa.exceptions.ApiErrorCode}.
     * </p>
     *
     * @param input DTO con los datos del nuevo usuario.
     * @return DTO de respuesta con los datos del usuario registrado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si los datos de entrada son inválidos,
     *         el correo es inválido o ya está en uso, o la residencia no existe.
     *
    public UserResponseDto save(UserDto input) throws RuntimeException{

        if (input.getNombre() == null || input.getApellido() == null || input.getEmail() == null || input.getPassword() == null ||
                input.getNombre().trim().isEmpty() || input.getApellido().trim().isEmpty() || input.getEmail().trim().isEmpty() || input.getPassword().isEmpty() ||
                input.getIdResidencia() == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }


        input.setEmail(input.getEmail().toLowerCase().trim());
        if(!EmailService.isEmailValid(input.getEmail().toLowerCase().trim())){
            throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
        }

        if (userRepository.findByEmail(input.getEmail()).isPresent()){
            throw new ApiException(ApiErrorCode.CORREO_DUPLICADO);
        }

        Residencia residenciaOpt = residenciaService.findById(input.getIdResidencia());
        if(residenciaOpt == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        User user = new User(input.getNombre(), input.getApellido(), input.getEmail(), input.getPassword());
        user.setResidencia(residenciaOpt);
        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    } */

    /**
     * Busca un usuario por su ID.
     * <p>
     * Este método consulta el repositorio de usuarios para recuperar una entidad {@link User}
     * que coincida con el ID proporcionado.
     * </p>
     *
     * @param idUsuario ID del usuario a buscar.
     * @return Objeto {@link User} si existe en la base de datos, o {@code null} si no se encuentra.
     */
    public User findById(Long idUsuario) {
        return userRepository.findById(idUsuario).orElse(null);
    }

    
    public void delete(Long idResidencia, Long idUser) {
        if (idResidencia == null || idUser == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
       //validar si existe ese usuario
        User userTmp = userRepository.findById(idUser)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USUARIO_INVALIDO));

        //Validar si pertenece a esa residencia
        if (userTmp.getResidencia() == null || !userTmp.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }

        // Comprobar juegos asociados
        if (userTmp.getRegistroJuegos() != null && !userTmp.getRegistroJuegos().isEmpty()){
            throw new ApiException(ApiErrorCode.REFERENCIAS_DEPENDIENTES);
        }

        userRepository.delete(userTmp);

    }



    public void deleteReferencies(Long idResidencia, Long idUser) {
        if (idResidencia == null || idUser == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        User userTmp = userRepository.findById(idUser)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USUARIO_INVALIDO));
        //Validar si pertenece a esa residencia
        if (!userTmp.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }
        // Desvincular el usuario de todos los registros de juego
        for(RegistroJuego reg : userTmp.getRegistroJuegos()){
            reg.setUsuario(null);
        }
        userTmp.setRegistroJuegos(new LinkedHashSet<>());
        userRepository.save(userTmp);
    }

    /**
     * Obtiene una imagen como recurso desde el sistema de archivos.
     * <p>
     * Este método busca un archivo de imagen en el directorio <code>src/main/resources/static/uploads</code>,
     * utilizando el nombre por defecto definido en {@link Conf#imageDefault}.
     * Si el archivo no existe o hay problemas al acceder a él, lanza una excepción controlada.
     * </p>
     *
     * @param filename Nombre del archivo solicitado (actualmente no se utiliza, se carga siempre la imagen por defecto).
     * @return {@link Resource} que representa la imagen cargada desde el sistema de archivos.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si el archivo no existe o no puede accederse.
     */
    public Resource getImage(String filename) {

        Path filePath = Paths.get("src/main/resources/static/uploads").resolve(Conf.imageDefault).normalize();
        Resource resource;
        try{
           resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ApiException(ApiErrorCode.PROBLEMAS_CON_FILE);
            }
        }catch (Exception e){
            throw new ApiException(ApiErrorCode.PROBLEMAS_CON_FILE);
        }
        return resource;
    }

    public UserResponseDto get(Long idResidencia, Long idUser) {
        if (idResidencia == null || idUser == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //Validar si existe ese usuario
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USUARIO_INVALIDO));

        //Validar si pertenece a esa residencia
        if (user.getResidencia() == null || !user.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }
        // Si todo es correcto, devolver el usuario
        return new UserResponseDto(user);

    }

    public List<UserResponseDto> getAll(Long idResidencia, String email, Boolean enabled, Long idJuego) {
        if (idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Validar si existe esa residencia
        Residencia residenciaTmp = residenciaService.findById(idResidencia);
        if (residenciaTmp == null) {
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        List<User> usuarios = userRepository.findByResidenciaId(idResidencia);

        // Filtrado por email (no-case-insensitive)
        if (email != null && !email.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(user -> user.getEmail().equalsIgnoreCase(email.trim().toLowerCase()))
                    .toList();
        }
        // Filtrado por habilitación
        if (enabled != null) {
            usuarios = usuarios.stream()
                    .filter(user -> user.isEnabled() == enabled)
                    .toList();
        }
        // Filtrado por ID de juego
        if (idJuego != null) {
            usuarios = usuarios.stream()
                    .filter(user -> user.getRegistroJuegos().stream()
                            .anyMatch(registroJuego -> registroJuego.getJuego().getId().equals(idJuego)))
                    .toList();
        }


        return usuarios.stream().map(UserResponseDto::new).toList();
    }

    public UserResponseDto update(Long idResidencia, Long idUser, UserDto input) {
        if (idResidencia == null || idUser == null)
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);

        //Validar si existe ese usuario
        User userTmp = userRepository.findById(idUser)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USUARIO_INVALIDO));
        //Validar si pertenece a esa residencia
        if (userTmp.getResidencia() == null || !userTmp.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }

        if (input != null){
            //Validar si el nombre es valido
            if (input.getNombre() != null && !input.getNombre().isEmpty()) {
                userTmp.setNombre(input.getNombre().trim());
            }
            //Validar si el apellido es valido
            if (input.getApellido() != null && !input.getApellido().isEmpty()) {
                userTmp.setApellido(input.getApellido().trim());
            }
            //
            //Validar si el email es valido
            input.setEmail(input.getEmail().trim().toLowerCase());
            if (EmailService.isEmailValid(input.getEmail())) {
                //Validar si el email ya existe
                if (userRepository.findByEmail(input.getEmail()).isPresent()) {
                    throw new ApiException(ApiErrorCode.CORREO_DUPLICADO);
                }
                userTmp.setEmail(input.getEmail());
            } else {
                throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
            }

        }
        return new UserResponseDto(userRepository.save(userTmp));
    }

    public UserResponseDto updatePassword(Long idResidencia, Long idUser, ChangePasswordUserDto input) {
        if (idResidencia == null || idUser == null || input == null ||
                input.getOldPassword() == null || input.getNewPassword() == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Validar si existe ese usuario
        User userTmp = userRepository.findById(idUser)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USUARIO_INVALIDO));
        //Validar si pertenece a esa residencia
        if (userTmp.getResidencia() == null || !userTmp.getResidencia().getId().equals(idResidencia)) {
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }
        //Validar si la contraseña es correcta
        if (!passwordEncoder.matches(input.getOldPassword(), userTmp.getPassword())) {
            throw new ApiException(ApiErrorCode.CONTRASENA_INCORRECTA); // Asegúrate de definir este código si no existe
        }

        userTmp.setPassword(passwordEncoder.encode(input.getNewPassword()));

        return new UserResponseDto(userRepository.save(userTmp));

    }
}
