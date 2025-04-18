package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.in.UserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
     */
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
    }

    /**
     * Filtra usuarios del sistema según residencia, email o estado habilitado.
     *
     * @param idResidencia ID de la residencia (opcional).
     * @param enable Estado de habilitación del usuario (opcional).
     * @param email Email del usuario a buscar (opcional).
     * @return Lista de usuarios que cumplen con los filtros.
     */
    public List<UserResponseDto> getUsers(Long idResidencia, Boolean enable, String email) {
        List<User> baseList;

        // Filtrado por residencia si se indica
        if (idResidencia != null) {
            baseList = userRepository.findByResidenciaId(idResidencia);
        } else {
            baseList = userRepository.findAll();
        }

        // Filtrado por email (case-insensitive)
        if (email != null && !email.isEmpty()) {
            baseList = baseList.stream()
                    .filter(user -> user.getEmail().equalsIgnoreCase(email.trim()))
                    .toList();
        }

        // Filtrado por habilitación
        if (enable != null) {
            baseList = baseList.stream()
                    .filter(user -> user.isEnabled() == enable)
                    .toList();
        }


        return baseList.stream().map(UserResponseDto::new).toList();
    }

    public User findById(Long idUsuario) {
        return userRepository.findById(idUsuario).orElse(null);
    }

    public UserResponseDto remove(Long idUser) {
        User userTmp = userRepository.findById(idUser).orElse(null);
        if (userTmp == null) {
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }
        userRepository.delete(userTmp);
        return new UserResponseDto(userTmp);
    }
}
