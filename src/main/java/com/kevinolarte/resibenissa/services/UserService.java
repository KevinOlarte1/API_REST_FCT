package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.UserDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
