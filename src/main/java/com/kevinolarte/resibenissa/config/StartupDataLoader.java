package com.kevinolarte.resibenissa.config;

import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupDataLoader {

    private final ResidenciaRepository residenciaRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        Residencia residenciaDefault = residenciaRepository.save(new Residencia("default", "default@default.com"));
        User userDefault  = new User("default", "default", "default@default.com", "default");
        userDefault.setResidencia(residenciaDefault);
        userDefault.setEnabled(true);
        userRepository.save(userDefault);
    }
}
