package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.UserDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * Guarda un nuevo usuario a partir de los datos del DTO.
     *
     * @param input DTO con los datos del nuevo usuario.
     * @return El usuario persistido.
     * @throws RuntimeException si hay campos vacíos, email inválido o residencia inexistente.
     */
    public User save(UserDto input) throws RuntimeException{

        if (input.getNombre() == null || input.getApellido() == null || input.getEmail() == null || input.getPassword() == null ||
                input.getNombre().isEmpty() || input.getApellido().isEmpty() || input.getEmail().isEmpty() || input.getPassword().isEmpty() ||
                input.getResidenciaId() == null){
            throw new RuntimeException("No puede faltar ningun campo");
        }

        if(!EmailService.isEmailValid(input.getEmail().toLowerCase().trim())){
            throw new RuntimeException("Email invalido");
        }

        if (userRepository.findByEmail(input.getEmail()).isPresent()){
            throw new RuntimeException("El email ya existe");
        }

        Residencia residenciaOpt = residenciaService.findById(input.getResidenciaId());
        if(residenciaOpt == null){
            throw new RuntimeException("Residencia no encontrada");
        }

        User user = new User(input.getNombre(), input.getApellido(), input.getEmail(), input.getPassword());
        user.setResidencia(residenciaOpt);

        return userRepository.save(user);
    }

    /**
     * Filtra usuarios del sistema según residencia, email o estado habilitado.
     *
     * @param idResidencia ID de la residencia (opcional).
     * @param enable Estado de habilitación del usuario (opcional).
     * @param email Email del usuario a buscar (opcional).
     * @return Lista de usuarios que cumplen con los filtros.
     */
    public List<User> getUsers(Long idResidencia, Boolean enable, String email) {
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

        return baseList;
    }

}
