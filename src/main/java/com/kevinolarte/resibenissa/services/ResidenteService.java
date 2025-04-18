package com.kevinolarte.resibenissa.services;


import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.repositories.ResidenteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Servicio encargado de gestionar la lógica relacionada con los residentes.
 * <p>
 * Permite registrar nuevos residentes, validando su información, así como
 * consultar residentes por ID, listar todos o filtrarlos por residencia.
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class ResidenteService {
    private final ResidenteRepository residenteRepository;
    private final ResidenciaService residenciaService;

    /**
     * Registra un nuevo residente en el sistema a partir de los datos proporcionados.
     * <p>
     * Este método valida que los campos requeridos estén presentes y sean válidos, incluyendo:
     * <ul>
     *   <li>Nombre y apellido no nulos ni vacíos.</li>
     *   <li>La fecha de nacimiento no puede ser posterior a la fecha actual.</li>
     *   <li>La residencia asociada debe existir en el sistema.</li>
     * </ul>
     * Si alguna de estas validaciones falla, se lanza una excepción {@link com.kevinolarte.resibenissa.exceptions.ApiException}
     * con un código de error correspondiente.
     * </p>
     *
     * @param input DTO con los datos del residente a registrar.
     * @return DTO de respuesta con los datos del residente registrado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si los datos de entrada son inválidos
     *         o si la residencia especificada no existe.
     */
    public ResidenteResponseDto save(ResidenteDto input)throws ApiException{
        if (input.getNombre() == null || input.getApellido() == null || input.getFechaNacimiento() == null ||
                input.getNombre().trim().isEmpty() || input.getApellido().trim().isEmpty()) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        if(input.getFechaNacimiento().isAfter(LocalDate.now())){
            throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
        }

        Residencia residencia = residenciaService.findById(input.getIdResidencia());
        if(residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        Residente residente = new Residente(input.getNombre(), input.getApellido(), input.getFechaNacimiento());
        residente.setResidencia(residencia);
        Residente residenteSaved = residenteRepository.save(residente);
        return new ResidenteResponseDto(residenteSaved);

    }

    /**
     * Busca un residente por su ID.
     *
     * @param id Identificador del residente.
     * @return El residente si existe, o {@code null} en caso contrario.
     */
    public Residente findById(long id) {

        return residenteRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene la lista de todos los residentes del sistema.
     *
     * @return Lista de residentes.
     */
    public List<Residente> findAll() {
        return (List<Residente>) residenteRepository.findAll();
    }

    /**
     * Obtiene la lista de todos los residentes del sistema.
     *
     * @return Lista de residentes.
     */
    public List<Residente> findByResidencia(Long id)throws RuntimeException {
        Residencia residencia = residenciaService.findById(id);
        if(residencia == null){
            throw new RuntimeException("Residencia no encontrada");
        }


        return residenteRepository.findByResidencia(residencia);
    }

    /**
     * Obtiener la lista de residentes filtrados por los siguentes parametros
     * @param idResidencia id de la residencia que pertenece
     * @param idResidente id del residente en especifico
     * @return una lista con el anterior filtrado.
     */
    public List<ResidenteResponseDto> getResidentes(Long idResidencia, Long idResidente){
        List<Residente> residentes;

        if (idResidente != null) {
            Residente residente = residenteRepository.findById(idResidente).orElse(null);
            if (residente != null) {
                if (!Objects.equals(residente.getResidencia().getId(), idResidencia)) {
                    return List.of();
                }
            }
            residentes = residente != null ? List.of(residente) : List.of();
        } else if (idResidencia != null) {
            residentes = residenteRepository.findByResidencia_Id(idResidencia);
        } else {
            residentes = residenteRepository.findAll();
        }

        return residentes.stream()
                .map(ResidenteResponseDto::new)
                .toList();
    }


    /**
     * Elimina un residente del sistema según su ID.
     * <p>
     * Este método busca el residente por su identificador y, si existe, lo elimina del repositorio.
     * Si el residente no existe, se lanza una excepción {@link com.kevinolarte.resibenissa.exceptions.ApiException}
     * con un código de error del enum {@link com.kevinolarte.resibenissa.exceptions.ApiErrorCode}.
     * </p>
     *
     * @param idResdente ID del residente que se desea eliminar.
     * @return DTO con la información del residente eliminado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si el residente no existe en el sistema.
     */
    public ResidenteResponseDto remove(Long idResdente) {
        Residente residenteTmp = residenteRepository.findById(idResdente).orElse(null);
        if(residenteTmp == null){
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }
        residenteRepository.delete(residenteTmp);

        return new ResidenteResponseDto(residenteTmp);
    }
}
