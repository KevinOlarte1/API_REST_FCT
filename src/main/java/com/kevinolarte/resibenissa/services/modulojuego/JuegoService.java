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
     * @param idResidencia ID de la residencia.
     * @param juegoDto Datos del juego a crear.
     * @return DTO con la información del juego creado.
     * @throws ApiException si falta algún campo obligatorio, la residencia no existe o el nombre está duplicado.
     */
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


    /**
     * Obtiene un juego por su ID y valida que pertenezca a la residencia indicada.
     *
     * @param idJuego ID del juego.
     * @param idResidencia ID de la residencia.
     * @return DTO del juego solicitado.
     * @throws ApiException si el ID es nulo, el juego no existe o no pertenece a la residencia.
     */
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

    /**
     * Elimina un juego si pertenece a la residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego a eliminar.
     * @throws ApiException si algún ID es nulo, el juego no existe o no pertenece a la residencia.
     */
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

    /**
     * Lista los juegos de una residencia con filtros opcionales por nombre y por registros máximos/mínimos.
     *
     * @param idResidencia ID de la residencia.
     * @param nombreJuego Filtro por nombre de juego (opcional).
     * @param maxRegistros Si es {@code true}, devuelve el juego con más registros;
     *                     si es {@code false}, el de menos registros;
     *                     si es {@code null}, todos.
     * @return Lista de juegos mapeados a DTO.
     * @throws ApiException si la residencia no existe o el ID es nulo.
     */
    public List<JuegoResponseDto> getAll(Long idResidencia, String nombreJuego, Boolean maxRegistros) {
        if (idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si existe la residencia
        Residencia residencia = residenciaService.findById(idResidencia);
        if (residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        List<Juego> juegoBaseList = juegoRepository.findByResidencia(residencia);

        System.out.println(juegoBaseList);
        // Filtrar por nombre de juego si se proporciona
        if (nombreJuego != null && !nombreJuego.trim().isEmpty()) {
            System.out.println("Filtrando por nombre");
            juegoBaseList = juegoBaseList.stream()
                    .filter(juego -> juego.getNombre().toLowerCase().contains(nombreJuego.toLowerCase()))
                    .toList();
        }

        //Filtrar por max registros o min.
        if (maxRegistros != null){
            System.out.println("Filtrando por max registros");
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
        System.out.println(juegoBaseList);
        // Mapear a DTO
        return juegoBaseList.stream()
                .map(JuegoResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza los datos de un juego si pertenece a una residencia específica.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param input DTO con los nuevos datos del juego.
     * @return DTO con la información del juego actualizado.
     * @throws ApiException si hay campos obligatorios faltantes, el juego no existe,
     *                      no pertenece a la residencia o el nuevo nombre está duplicado.
     */
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
