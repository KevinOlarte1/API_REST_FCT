package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.JuegoDto;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.JuegoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de la lógica relacionada con los juegos.
 * <p>
 * Permite registrar nuevos juegos, obtenerlos por ID o por residencia,
 * y verificar restricciones como la unicidad del nombre dentro de una residencia.
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class JuegoService {

    private final JuegoRepository juegoRepository;
    private final ResidenciaService residenciaService;


    /**
     * Guarda un nuevo juego en la base de datos a partir de los datos recibidos en el DTO.
     *
     * @param juegoDto DTO con la información del juego a registrar.
     * @return El juego creado y persistido.
     * @throws RuntimeException si faltan datos, la residencia no existe o el juego ya está registrado en esa residencia.
     */
    public Juego save(JuegoDto juegoDto)throws RuntimeException{
        if (juegoDto.getNombre() == null || juegoDto.getNombre().isEmpty() || juegoDto.getIdResidencia() == null){
            throw new RuntimeException("Ningún campo puede ser nulo o vacio");
        }

        Residencia residencia = residenciaService.findById(juegoDto.getIdResidencia());
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

    /**
     * Busca un juego por su ID.
     *
     * @param id ID del juego.
     * @return El juego si existe, o null si no se encuentra.
     */
    public Juego findById(Long id){
        return juegoRepository.findById(id).orElse(null);
    }

    /**
     * Devuelve una lista de juegos filtrada por ID de juego o ID de residencia.
     * Si ambos parámetros son nulos, devuelve todos los juegos.
     *
     * @param idJuego     ID específico del juego.
     * @param idResidencia ID de la residencia.
     * @return Lista de juegos correspondiente al filtro aplicado.
     */
    public List<Juego> getJuegos(Long idJuego, Long idResidencia) {
        if (idJuego != null) {
            Juego juego = findById(idJuego);
            return juego != null ? List.of(juego) : List.of();
        }

        if (idResidencia != null) {
            return juegoRepository.findByResidenciaId(idResidencia);
        }

        return juegoRepository.findAll();
    }

}
