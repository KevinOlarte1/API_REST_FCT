package com.kevinolarte.resibenissa.services;


import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.repositories.ResidenteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica relacionada con los residentes.
 * <p>
 * Permite registrar nuevos residentes, validando su información, así como
 * consultar residentes por ID, listar todos o filtrarlos por residencia.
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class ResidenteService {
    private final ResidenteRepository residenteRepository;
    private final ResidenciaService residenciaService;

    /**
     * Guarda un nuevo residente a partir de los datos del DTO.
     *
     * @param input DTO con los datos del residente.
     * @return El residente registrado y persistido.
     * @throws RuntimeException si hay campos inválidos o falta de residencia.
     */
    public Residente save(ResidenteDto input)throws RuntimeException{
        if (input.getNombre() == null || input.getApellido() == null || input.getFechaNacimiento() == null ||
                input.getNombre().isEmpty() || input.getApellido().isEmpty()) {
            throw new RuntimeException("No campos vacios");
        }

        if(input.getFechaNacimiento().isAfter(LocalDate.now())){
            throw new RuntimeException("Fecha nacimiento es invalida");
        }

        Residencia residencia = residenciaService.findById(input.getResidenciaId());
        if(residencia == null){
            throw new RuntimeException("Residencia no encontrada");
        }

        Residente residente = new Residente(input.getNombre(), input.getApellido(), input.getFechaNacimiento());
        residente.setResidencia(residencia);
        return residenteRepository.save(residente);

    }

    /**
     * Busca un residente por su ID.
     *
     * @param id Identificador del residente.
     * @return El residente si existe, o {@code null} en caso contrario.
     */
    public Residente findById(long id) {

        return residenteRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene la lista de todos los residentes del sistema.
     *
     * @return Lista de residentes.
     */
    public List<Residente> findAll() {
        return (List<Residente>) residenteRepository.findAll();
    }

    /**
     * Obtiene la lista de todos los residentes del sistema.
     *
     * @return Lista de residentes.
     */
    public List<Residente> findByResidencia(Long id)throws RuntimeException {
        Residencia residencia = residenciaService.findById(id);
        if(residencia == null){
            throw new RuntimeException("Residencia no encontrada");
        }


        return residenteRepository.findByResidencia(residencia);
    }
}
