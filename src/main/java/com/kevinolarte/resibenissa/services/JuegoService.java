package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.JuegoDto;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.JuegoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JuegoService {

    private final JuegoRepository juegoRepository;
    private final ResidenciaService residenciaService;



    public Juego save(JuegoDto juegoDto)throws RuntimeException{
        if (juegoDto.getNombre() == null || juegoDto.getNombre().isEmpty() || juegoDto.getResidenteId() == null){
            throw new RuntimeException("Ningún campo puede ser nulo o vacio");
        }
        Residencia residencia = residenciaService.findById(juegoDto.getResidenteId());
        if (residencia == null){
            throw new RuntimeException("La residencia no existe");
        }

        // Comprobar si ya existe un juego con ese nombre en esa residencia
        boolean exists = juegoRepository.existsByNombreAndResidenciaId(juegoDto.getNombre(), residencia.getId());
        if (exists) {
            throw new RuntimeException("Ya existe un juego con ese nombre en esta residencia");
        }

        Juego juego = new Juego(juegoDto.getNombre());
        juego.setResidencia(residencia);
        juegoRepository.save(juego);
        return juego;

    }

    public List<Juego> findAll(){
        return (List<Juego>) juegoRepository.findAll();
    }

    public Juego findById(Long id){
        return juegoRepository.findById(id).orElse(null);
    }
}
