package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.LogContext;
import com.kevinolarte.resibenissa.enums.CategoriaLog;
import com.kevinolarte.resibenissa.models.Logger;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.LoggerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoggerService {

    private final LoggerRepository loggerRepository;

    public LoggerService(LoggerRepository loggerRepository) {
        this.loggerRepository = loggerRepository;
    }

    public void  registrarLog(String endpoint, String metodo, String descripcion) {
        Logger log = new Logger(
                endpoint,
                metodo,
                descripcion
        );
        Logger saved = loggerRepository.save(log);
        LogContext.setCurrentLogId(saved.getId());


    }

    public void registrarLogError(String descripcion) {
        Logger log = new Logger(null, null, descripcion);

        Long logId = LogContext.getCurrentLogId();
        if (logId != null) {
            loggerRepository.findById(logId).ifPresent(log::setPadre);
        }

        loggerRepository.save(log);
    }


}
