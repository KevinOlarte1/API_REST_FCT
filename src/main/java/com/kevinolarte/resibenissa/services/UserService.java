package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.UserDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ResidenciaService residenciaService;

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

    public List<User> getUsers(Long idResidencia, Boolean enable, String email) {
        List<User> baseList;

        // Primero comprobamos si hay residencia
        if (idResidencia != null) {
            baseList = userRepository.findByResidenciaId(idResidencia);
        } else {
            baseList = userRepository.findAll();
        }

        // Luego filtramos en memoria si se indica el email
        if (email != null && !email.isEmpty()) {
            baseList = baseList.stream()
                    .filter(user -> user.getEmail().equalsIgnoreCase(email))
                    .toList();
        }

        // Por último filtramos si enable no es null
        if (enable != null) {
            baseList = baseList.stream()
                    .filter(user -> user.isEnabled() == enable)
                    .toList();
        }

        return baseList;
    }

}
