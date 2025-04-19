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
 * Servicio que gestiona la lógica de negocio relacionada con los juegos.
 * <p>
 * Permite crear juegos nuevos validados y consultarlos por ID o por residencia.
 * </p>
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class JuegoService {

    private final JuegoRepository juegoRepository;
    private final ResidenciaService residenciaService;


    /**
     * Guarda un nuevo juego en el sistema.
     * <p>
     * Se validan los siguientes aspectos:
     * <ul>
     *   <li>El nombre del juego no puede estar vacío.</li>
     *   <li>Debe estar asociado a una residencia válida existente.</li>
     *   <li>No debe existir ya un juego con el mismo nombre en la misma residencia.</li>
     * </ul>
     * </p>
     *
     * @param juegoDto DTO con los datos del juego a registrar.
     * @return DTO con la información del juego registrado.
     * @throws ApiException si faltan campos, la residencia no existe o el nombre ya está en uso en esa residencia.
     */
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
     * @param id ID del juego a buscar.
     * @return Entidad {@link Juego} si existe, o {@code null} si no se encuentra.
     */
    public Juego findById(Long id){
        return juegoRepository.findById(id).orElse(null);
    }

    /**
     * Recupera una lista de juegos según filtros opcionales.
     * <p>
     * Comportamientos posibles:
     * <ul>
     *   <li>Si se proporciona <code>idJuego</code>, devuelve ese juego (si existe).</li>
     *   <li>Si se proporciona <code>idResidencia</code>, devuelve todos los juegos de esa residencia.</li>
     *   <li>Si no se proporciona ningún parámetro, devuelve todos los juegos existentes.</li>
     * </ul>
     * </p>
     *
     * @param idJuego ID específico del juego a recuperar (opcional).
     * @param idResidencia ID de la residencia para filtrar juegos (opcional).
     * @return Lista de juegos convertidos a {@link JuegoResponseDto}.
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

    /**
     * Elimina un juego del sistema según su ID.
     * <p>
     * Este método busca el juego por su identificador y, si lo encuentra, lo elimina del repositorio.
     * Si el juego no existe, lanza una excepción {@link com.kevinolarte.resibenissa.exceptions.ApiException}
     * con el código de error {@link com.kevinolarte.resibenissa.exceptions.ApiErrorCode#JUEGO_INVALIDO}.
     * </p>
     *
     * @param idJuego ID del juego que se desea eliminar.
     * @return DTO con la información del juego eliminado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si el juego no existe en el sistema.
     */
    public JuegoResponseDto remove(Long idJuego) {
        Juego juegTmp = juegoRepository.findById(idJuego).orElse(null);

        if(juegTmp == null){
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        }

        juegoRepository.delete(juegTmp);
        return new JuegoResponseDto(juegTmp);
    }
}
