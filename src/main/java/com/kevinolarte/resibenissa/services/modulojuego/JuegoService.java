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
 * Permite crear, consultar, listar, actualizar y eliminar juegos, todos asociados a una residencia específica.
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
     * Registra un nuevo juego en una residencia.
     * <p>
     * Realiza validaciones sobre los campos obligatorios, existencia de la residencia y unicidad del nombre del juego.
     * </p>
     *
     * @param juegoDto Datos del juego a crear.
     * @return DTO con la información del juego creado.
     * @throws ApiException si falta algún campo obligatorio, la residencia no existe o el nombre está duplicado.
     */
    public JuegoResponseDto save(JuegoDto juegoDto)throws ApiException{
        if (juegoDto.getNombre() == null || juegoDto.getNombre().trim().isEmpty()){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }



        // Comprobar si ya existe un juego con ese nombre en esa residencia
        boolean exists = juegoRepository.existsByNombreIgnoreCase((juegoDto.getNombre().trim().toLowerCase()));
        if (exists) {
            throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }

        Juego juego = new Juego(juegoDto.getNombre().trim().toLowerCase());
        return new JuegoResponseDto( juegoRepository.save(juego));

    }





    /**
     * Lista los juegos de una residencia con filtros opcionales por nombre y por registros máximos/mínimos.
     *
     * @param nombreJuego Filtro por nombre de juego (opcional).
     * @param maxRegistros Si es {@code true}, devuelve el juego con más registros;
     *                     si es {@code false}, el de menos registros;
     *                     si es {@code null}, todos.
     * @return Lista de juegos mapeados a DTO.
     * @throws ApiException si la residencia no existe o el ID es nulo.
     */
    public List<JuegoResponseDto> getAll(String nombreJuego, Boolean maxRegistros) {

        List<Juego> juegoBaseList = juegoRepository.findAll();

        if (nombreJuego != null)
            juegoBaseList = juegoRepository.findByNombreContainingIgnoreCase(nombreJuego.trim().toLowerCase());

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

    /**
     * Obtiene un juego por su ID y valida que pertenezca a la residencia indicada.
     *
     * @param idJuego ID del juego
     * @return DTO del juego solicitado.
     * @throws ApiException si el ID es nulo, el juego no existe o no pertenece a la residencia.
     */
    public JuegoResponseDto get(Long idJuego) {
       if (idJuego == null){
           throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
       }
        //Comprobar si existe el juego
        Juego juegoTmp = juegoRepository.findById(idJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.JUEGO_INVALIDO));

        return new JuegoResponseDto(juegoTmp);

    }




    /**
     * Elimina un juego si pertenece a la residencia.
     *
     * @param idJuego ID del juego a eliminar.
     * @throws ApiException si algún ID es nulo, el juego no existe o no pertenece a la residencia.
     */
    public void delete(Long idJuego) {
        if (idJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //Comprobar si existe el juego
        Juego juegoTmp = juegoRepository.findById(idJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.JUEGO_INVALIDO));

        juegoRepository.delete(juegoTmp);

    }




    /**
     * Actualiza los datos de un juego si pertenece a una residencia específica.
     *
     * @param idJuego ID del juego.
     * @param input DTO con los nuevos datos del juego.
     * @return DTO con la información del juego actualizado.
     * @throws ApiException si hay campos obligatorios faltantes, el juego no existe,
     *                      no pertenece a la residencia o el nuevo nombre está duplicado.
     */
    public JuegoResponseDto update( Long idJuego, JuegoDto input) {
        if (idJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si existe el juego
        Juego juegoTmp = juegoRepository.findById(idJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.JUEGO_INVALIDO));

        //Comprobar si ya existe un juego con ese nombre en esa residencia
        if (input != null && input.getNombre() != null && !input.getNombre().trim().isEmpty()){
            boolean exists = juegoRepository.existsByNombreIgnoreCase(input.getNombre().trim().toLowerCase());
            if (exists) {
                throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
            }
            juegoTmp.setNombre(input.getNombre().trim().toLowerCase());
        }

        return new JuegoResponseDto(juegoRepository.save(juegoTmp));
    }








    /**
     * Busca un juego por su ID.
     *
     * @param idJuego ID del juego a buscar.
     * @return El juego encontrado.
     * @throws ApiException si el ID es nulo o el juego no existe.
     */
    public Juego getJuego(Long idJuego) {
        if (idJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si existe el juego

        return juegoRepository.findById(idJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.JUEGO_INVALIDO));
    }
}
