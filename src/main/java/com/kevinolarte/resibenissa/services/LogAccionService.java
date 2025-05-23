package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.enums.CategoriaLog;
import com.kevinolarte.resibenissa.models.LogAccion;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.LogAccionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LogAccionService {

    private final LogAccionRepository logAccionRepository;

    public void registrarLogAccion(String nombre, String descripcion, User user, CategoriaLog categoriaLog) {
        LogAccion logAccion = new LogAccion(nombre, descripcion, user.getEmail());
        logAccionRepository.findById(categoriaLog.getValor()).ifPresent(logAccion::setLogPadre);
        logAccionRepository.save(logAccion);

    }
}
