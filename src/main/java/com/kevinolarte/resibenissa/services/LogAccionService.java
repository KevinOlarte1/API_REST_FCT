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

    /**
     * Registra una acción en el log de acciones.
     * <p>
     * Crea un nuevo registro de acción con el nombre, descripción y usuario proporcionados.
     * Si la categoría de log tiene un registro padre, lo establece como tal.
     * </p>
     *
     * @param nombre       Nombre de la acción registrada.
     * @param descripcion  Descripción detallada de la acción.
     * @param user         Usuario que realiza la acción.
     * @param categoriaLog Categoría del log donde se registra la acción.
     */
    public void registrarLogAccion(String nombre, String descripcion, User user, CategoriaLog categoriaLog) {
        LogAccion logAccion = new LogAccion(nombre, descripcion, user.getEmail());
        logAccionRepository.findById(categoriaLog.getValor()).ifPresent(logAccion::setLogPadre);
        logAccionRepository.save(logAccion);

    }
}
