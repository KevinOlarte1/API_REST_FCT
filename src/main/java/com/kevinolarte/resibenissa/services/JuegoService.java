package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.in.JuegoDto;
import com.kevinolarte.resibenissa.dto.out.JuegoResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.JuegoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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



    public JuegoResponseDto save(JuegoDto juegoDto)throws ApiException{
        if (juegoDto.getNombre() == null || juegoDto.getNombre().trim().isEmpty() || juegoDto.getIdResidencia() == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        Residencia residencia = residenciaService.findById(juegoDto.getIdResidencia());
        if (residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        // Comprobar si ya existe un juego con ese nombre en esa residencia
        boolean exists = juegoRepository.existsByNombreAndResidenciaId(juegoDto.getNombre(), residencia.getId());
        if (exists) {
            throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }

        Juego juego = new Juego(juegoDto.getNombre());
        juego.setResidencia(residencia);
        Juego juegoSafe = juegoRepository.save(juego);
        return new JuegoResponseDto(juegoSafe);

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
    public List<JuegoResponseDto> getJuegos(Long idJuego, Long idResidencia) {
        if (idJuego != null) {
            Juego juego = findById(idJuego);
            return juego != null ? List.of(new JuegoResponseDto(juego)) : List.of();
        }

        if (idResidencia != null) {
            return juegoRepository.findByResidenciaId(idResidencia).stream()
                    .map(JuegoResponseDto::new)
                    .collect(Collectors.toList());
        }

        return juegoRepository.findAll().stream()
                .map(JuegoResponseDto::new)
                .collect(Collectors.toList());
    }

}
