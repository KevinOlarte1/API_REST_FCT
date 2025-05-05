package com.kevinolarte.resibenissa.services.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.JuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.JuegoResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.modulojuego.JuegoRepository;
import com.kevinolarte.resibenissa.services.ResidenciaService;
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



    public JuegoResponseDto save(Long idResidencia, JuegoDto juegoDto)throws ApiException{
        if (juegoDto.getNombre() == null || juegoDto.getNombre().trim().isEmpty() || idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        Residencia residencia = residenciaService.findById(idResidencia);
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


    public JuegoResponseDto get(Long idJuego, Long idResidencia) {
       if (idResidencia == null || idJuego == null){
           throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
       }
        //Comprobar si existe el juego
        Juego juegoTmp = juegoRepository.findById(idJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.JUEGO_INVALIDO));

        //Comprobar si pertenece a la residencia
        if (!juegoTmp.getResidencia().getId().equals(idResidencia)){
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        }

        return new JuegoResponseDto(juegoTmp);

    }


    public void delete(Long idResidencia, Long idJuego) {
        if (idResidencia == null || idJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //Comprobar si existe el juego
        Juego juegoTmp = juegoRepository.findById(idJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.JUEGO_INVALIDO));
        //Comprobar si pertenece a la residencia
        if (!juegoTmp.getResidencia().getId().equals(idResidencia)){
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        }

        juegoRepository.delete(juegoTmp);

    }

    public List<JuegoResponseDto> getAll(Long idResidencia, String nombreJuego, Boolean maxRegistros) {
        if (idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si existe la residencia
        Residencia residencia = residenciaService.findById(idResidencia);
        if (residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        List<Juego> juegoBaseList = juegoRepository.findByResidenciaId(idResidencia);

        // Filtrar por nombre de juego si se proporciona
        if (nombreJuego != null && !nombreJuego.trim().isEmpty()) {
            juegoBaseList = juegoBaseList.stream()
                    .filter(juego -> juego.getNombre().toLowerCase().contains(nombreJuego.toLowerCase()))
                    .toList();
        }

        //Filtrar por max registros o min.
        if (maxRegistros != null){
            if (maxRegistros){
                Juego juego = juegoBaseList.get(0);
                for(Juego j : juegoBaseList){
                    if (j.getRegistro().size() > juego.getRegistro().size()){
                        juego = j;
                    }
                }
                juegoBaseList = List.of(juego);
            }
            else {
                Juego juego = juegoBaseList.get(0);
                for(Juego j : juegoBaseList){
                    if (j.getRegistro().size() < juego.getRegistro().size()){
                        juego = j;
                    }
                }
                juegoBaseList = List.of(juego);
            }
        }
        // Mapear a DTO
        return juegoBaseList.stream()
                .map(JuegoResponseDto::new)
                .collect(Collectors.toList());
    }

    public JuegoResponseDto update(Long idResidencia, Long idJuego, JuegoDto input) {
        if (idResidencia == null || idJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si existe el juego
        Juego juegoTmp = juegoRepository.findById(idJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.JUEGO_INVALIDO));
        //Comprobar si pertenece a la residencia
        if (!juegoTmp.getResidencia().getId().equals(idResidencia)){
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        }
        //Comprobar si ya existe un juego con ese nombre en esa residencia
        if (input != null && input.getNombre() != null && !input.getNombre().trim().isEmpty()){
            boolean exists = juegoRepository.existsByNombreAndResidenciaId(input.getNombre(), idResidencia);
            if (exists) {
                throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
            }
            juegoTmp.setNombre(input.getNombre());
        }
        // Guardar cambios
        Juego juegoSafe = juegoRepository.save(juegoTmp);
        return new JuegoResponseDto(juegoSafe);
    }
}
