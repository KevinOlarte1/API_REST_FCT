package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.ResidenciaDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que gestiona la lógica relacionada con entidades {@link Residencia}.
 * <p>
 * Permite crear nuevas residencias, validando unicidad y formato del correo electrónico,
 * así como obtener residencias por ID o listarlas todas.
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class ResidenciaService {

    private final ResidenciaRepository residenciaRepository;

    /**
     * Crea una nueva residencia en el sistema a partir de los datos recibidos.
     *
     * @param input DTO con nombre y correo electrónico.
     * @return La entidad {@link Residencia} guardada.
     * @throws RuntimeException si hay campos vacíos, email inválido o duplicación.
     */
    public Residencia save(ResidenciaDto input) throws RuntimeException{
        if (input.getNombre() == null || input.getEmail() == null
                || input.getNombre().isEmpty() || input.getEmail().isEmpty()){
            throw new RuntimeException("El nombre y el correo son obligatorio");
        }

        if (!EmailService.isEmailValid(input.getEmail())){
            throw new RuntimeException("Email invalido");
        }

        Optional<Residencia> residenciaTmp = residenciaRepository.findByEmail(input.getEmail());
        Optional<Residencia> residenciaTmp2 = residenciaRepository.findByNombre(input.getNombre());

        if(residenciaTmp.isPresent()){
            throw new RuntimeException("Este email ya esta registrado");
        }
        if(residenciaTmp2.isPresent()){
            throw new RuntimeException("Este nombre ya esta registrado");
        }

        Residencia residencia = new Residencia(input.getNombre(), input.getEmail());
        return residenciaRepository.save(residencia);
    }

    /**
     * Obtiene todas las residencias registradas en el sistema.
     *
     * @return Lista de todas las entidades {@link Residencia}.
     */
    public List<Residencia> findAll(){
        return (List<Residencia>) residenciaRepository.findAll();
    }

    /**
     * Busca una residencia por su ID.
     *
     * @param id Identificador de la residencia.
     * @return La residencia encontrada o {@code null} si no existe.
     */
    public Residencia findById(Long id){
        Optional<Residencia> residenciaTmp = residenciaRepository.findById(id);
        return residenciaTmp.orElse(null);
    }
}
