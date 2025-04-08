package com.kevinolarte.resibenissa.services;


import com.kevinolarte.resibenissa.dto.ResidenteDto;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.repositories.ResidenteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResidenteService {
    private final ResidenteRepository residenteRepository;
    private final ResidenciaService residenciaService;

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

    public Residente findById(long id) {
        Optional<Residente> residente = residenteRepository.findById(id);
        return residente.orElse(null);
    }
}
