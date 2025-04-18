package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
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
     * <p>
     * Este método valida que el nombre y correo no estén vacíos, que el correo tenga un formato válido,
     * y que tanto el nombre como el correo no estén ya registrados previamente en la base de datos.
     *
     * @param input DTO que contiene el nombre y correo electrónico de la residencia a crear.
     * @return La entidad {@link Residencia} creada y persistida.
     * @throws ApiException si:
     * <ul>
     *   <li>Faltan campos obligatorios ({@link ApiErrorCode#CAMPOS_OBLIGATORIOS})</li>
     *   <li>El correo no tiene un formato válido ({@link ApiErrorCode#CORREO_INVALIDO})</li>
     *   <li>El correo ya está registrado ({@link ApiErrorCode#CORREO_DUPLICADO})</li>
     *   <li>El nombre ya está registrado ({@link ApiErrorCode#NOMBRE_DUPLICADO})</li>
     * </ul>
     */
    public ResidenciaResponseDto save(ResidenciaDto input) throws RuntimeException{
        if (input.getNombre() == null || input.getEmail() == null
                || input.getNombre().trim().isEmpty() || input.getEmail().trim().isEmpty()){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        input.setEmail(input.getEmail().toLowerCase().trim());
        if (!EmailService.isEmailValid(input.getEmail())){
            throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
        }

        Optional<Residencia> residenciaTmp = residenciaRepository.findByEmail(input.getEmail());
        Optional<Residencia> residenciaTmp2 = residenciaRepository.findByNombre(input.getNombre());
        if(residenciaTmp.isPresent()){
            throw new ApiException(ApiErrorCode.CORREO_DUPLICADO);
        }
        if(residenciaTmp2.isPresent()){
            throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }

        Residencia residencia = new Residencia(input.getNombre(), input.getEmail());
        Residencia savedResidencia = residenciaRepository.save(residencia);
        return new ResidenciaResponseDto(savedResidencia);
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


    /**
     * Obtiene una lista de residencias, ya sea todas o una específica si se proporciona un ID.
     * <p>
     * Si se proporciona el parámetro {@code idResidencia}, el método intenta obtener únicamente
     * la residencia correspondiente a ese ID. Si no se proporciona, devuelve todas las residencias disponibles.
     * <p>
     * El resultado se convierte en una lista de objetos {@link ResidenciaResponseDto}, que representan
     * la información estructurada de cada residencia para ser expuesta como respuesta.
     *
     * @param idResidencia ID de la residencia a buscar (opcional).
     * @return Lista de {@link ResidenciaResponseDto} con la información de una o varias residencias.
     */
    public List<ResidenciaResponseDto> getResidencias(Long idResidencia) {
        List<Residencia> resi;
        if (idResidencia == null)
            resi = residenciaRepository.findAll();
        else
            resi = residenciaRepository.findById(idResidencia).stream().toList();

        //Mappeamos a esa  nueva clase Response Dto.
        return resi.stream()
                .map(ResidenciaResponseDto::new)
                .toList();
    }

    /**
     * Elimina una residencia del sistema según su ID.
     * <p>
     * Este método busca la residencia por su identificador único. Si la encuentra, la elimina del repositorio.
     * Si no existe, lanza una excepción personalizada {@link com.kevinolarte.resibenissa.exceptions.ApiException}
     * con el código {@link com.kevinolarte.resibenissa.exceptions.ApiErrorCode#RESIDENCIA_INVALIDO}.
     * </p>
     *
     * @param idResidencia ID de la residencia que se desea eliminar.
     * @return DTO con la información de la residencia eliminada.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si no se encuentra la residencia especificada.
     */
    public ResidenciaResponseDto remove(Long idResidencia) {
        Residencia residenciaTmp = residenciaRepository.findById(idResidencia).orElse(null);
        if(residenciaTmp == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }
        residenciaRepository.delete(residenciaTmp);
        return new ResidenciaResponseDto(residenciaTmp);
    }
}
