package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaPublicResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona la lógica de negocio relacionada con entidades {@link Residencia}.
 * <p>
 * Permite crear, obtener, buscar por ID y eliminar residencias.
 * </p>
 *
 * @author : Kevin Olarte
 */
@Service
@AllArgsConstructor
public class ResidenciaService {

    private final ResidenciaRepository residenciaRepository;


    /**
     * Crea una nueva residencia en el sistema a partir de los datos recibidos.
     * <p>
     * Valida que el nombre y correo no estén vacíos, que el correo tenga un formato válido,
     * y que tanto el nombre como el correo no estén ya registrados.
     * </p>
     *
     * @param input DTO que contiene el nombre y correo de la residencia.
     * @return {@link ResidenciaResponseDto} de la residencia creada.
     * @throws ApiException en caso de errores de validación o duplicados.
     */
    public ResidenciaResponseDto save(ResidenciaDto input) throws RuntimeException{
        if (input.getNombre() == null || input.getEmail() == null
                || input.getNombre().trim().isEmpty() || input.getEmail().trim().isEmpty()){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar formato del correo electrónico
        input.setEmail(input.getEmail().toLowerCase().trim());
        if (!EmailService.isEmailValid(input.getEmail())){
            throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
        }

        // Comprobar si ya existe una residencia con ese correo o nombre
        Optional<Residencia> residenciaTmp = residenciaRepository.findByEmail(input.getEmail());
        Optional<Residencia> residenciaTmp2 = residenciaRepository.findByNombre(input.getNombre());
        if(residenciaTmp.isPresent()){
            throw new ApiException(ApiErrorCode.CORREO_DUPLICADO);
        }
        if(residenciaTmp2.isPresent()){
            throw new ApiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }

        Residencia residencia = new Residencia(input.getNombre(), input.getEmail());
        return new ResidenciaResponseDto(residenciaRepository.save(residencia));
    }

    /**
     * Busca una residencia por su ID.
     *
     * @param id ID de la residencia.
     * @return {@link Residencia} encontrada, o {@code null} si no existe.
     */
    public Residencia findById(Long id){
        Optional<Residencia> residenciaTmp = residenciaRepository.findById(id);
        return residenciaTmp.orElse(null);
    }

    /**
     * Obtiene una residencia a partir de su ID validando su existencia.
     *
     * @param idResidencia ID de la residencia a recuperar.
     * @return {@link ResidenciaResponseDto} de la residencia encontrada.
     * @throws ApiException si el ID es nulo o no existe una residencia con ese ID.
     */
    public ResidenciaResponseDto get(Long idResidencia) {
        Residencia resi;

        // Comprobar si el ID de residencia es nulo
        if (idResidencia == null)
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        else
            resi = residenciaRepository.findById(idResidencia)
                    .orElseThrow(() -> new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO));


        return new ResidenciaResponseDto(resi);
    }


    /**
     * Elimina una residencia a partir de su ID.
     * <p>
     * Si no se encuentra la residencia, lanza una excepción.
     * </p>
     *
     * @param idResidencia ID de la residencia a eliminar.
     * @throws ApiException si no se encuentra la residencia especificada.
     */
    public void remove(Long idResidencia) {
        Residencia residenciaTmp = residenciaRepository.findById(idResidencia).orElse(null);

        if(residenciaTmp == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        residenciaRepository.delete(residenciaTmp);
    }

    /**
     * Obtiene una lista de todas las residencias en el sistema. con solo el nombre , correo e id.
     * @return Lista de residencias públicas.
     */
    public List<ResidenciaPublicResponseDto> getAll() {
        return residenciaRepository.findAll()
                .stream().map(ResidenciaPublicResponseDto::new)
                .collect(Collectors.toList());
    }
}
