package com.kevinolarte.resibenissa.services;


import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
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
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar la lógica relacionada con los residentes.
 * <p>
 * Permite registrar nuevos residentes, consultar, actualizar y eliminar residentes,
 * validando su información y asociándolos a una residencia existente.
 * </p>
 *
 * @author : Kevin Olarte
 */
@Service
@AllArgsConstructor
public class ResidenteService {
    private final ResidenteRepository residenteRepository;
    private final ResidenciaService residenciaService;

    /**
     * Crea un nuevo residente asociado a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param input DTO con los datos del residente a registrar.
     * @return DTO del residente creado.
     * @throws ApiException si faltan campos, datos inválidos o documentos duplicados.
     */
    public ResidenteResponseDto add(Long idResidencia, ResidenteDto input)throws ApiException{
        if (input.getNombre() == null || input.getApellido() == null || input.getFechaNacimiento() == null || input.getDocumentoIdentidad() == null ||
                input.getNombre().trim().isEmpty() || input.getApellido().trim().isEmpty() || input.getDocumentoIdentidad().trim().isEmpty() || idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar el formato del documento de identidad
        input.setDocumentoIdentidad(input.getDocumentoIdentidad().trim().toUpperCase());
        if (input.getDocumentoIdentidad().length() != 8){
            throw new ApiException(ApiErrorCode.DOCUMENTO_INVALIDO);
        }

        // Validar que la fecha de nacimiento no sea futura
        if(input.getFechaNacimiento().isAfter(LocalDate.now())){
            throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
        }

        // Validar que la residencia existe
        Residencia residencia = residenciaService.findById(idResidencia);
        if(residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        // Validar que no existe otro residente con el mismo documento de identidad en cualquier residencia
        Residente residenteDup = residenteRepository.findByDocuemntoIdentidad(input.getDocumentoIdentidad());
        if (residenteDup != null){
            throw new ApiException(ApiErrorCode.DOCUMENTO_DUPLICADO);
        }

        // Crear el nuevo residente
        Residente residente = new Residente(input.getNombre(), input.getApellido(), input.getFechaNacimiento(), input.getDocumentoIdentidad());
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
     * Obtiene un residente validando su pertenencia a una residencia específica.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @return DTO del residente encontrado.
     * @throws ApiException si los IDs no corresponden o no existe el residente.
     */
    public ResidenteResponseDto get(Long idResidencia, Long idResidente){

        Residente residenteTmp = getResidente(idResidencia, idResidente);

        return new ResidenteResponseDto(residenteTmp);


    }

    /**
     * Obtiene todos los residentes de una residencia, con opción de filtrar.
     *
     * @param idResidencia ID de la residencia.
     * @param filtre Filtros aplicables (documento de identidad, fecha de nacimiento, año o mes).
     * @return Lista de DTOs de residentes filtrados.
     * @throws ApiException si no existe la residencia.
     */
    public List<ResidenteResponseDto> getAll(Long idResidencia, ResidenteDto filtre){
        if (idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que la residencia existe
        Residencia residencia = residenciaService.findById(idResidencia);
        // Comprobar que la residencia existe
        if(residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        // Obtener todos los residentes de la residencia
        List<Residente> residentesBaseList =  residenteRepository.findByResidencia(residencia);

        //Filtrar
        if (filtre != null) {
            if (filtre.getDocumentoIdentidad() != null){
                filtre.setDocumentoIdentidad(filtre.getDocumentoIdentidad().trim().toUpperCase());
                // Filtrar por documento de identidad
                residentesBaseList = residentesBaseList.stream()
                        .filter(r -> r.getDocuemntoIdentidad().equals(filtre.getDocumentoIdentidad()))
                        .toList();
            }else{
                if (filtre.getFechaNacimiento() != null){
                    // Filtrar por fecha de nacimiento
                    residentesBaseList = residentesBaseList.stream()
                            .filter(r -> r.getFechaNacimiento().equals(filtre.getFechaNacimiento()))
                            .toList();
                }else {
                    if (filtre.getYear() != null){
                        // Filtrar por año de nacimiento
                        residentesBaseList = residentesBaseList.stream()
                                .filter(r -> r.getFechaNacimiento().getYear() == filtre.getYear())
                                .toList();
                    }
                    if (filtre.getMonth() != null){
                        // Filtrar por mes de nacimiento
                        residentesBaseList = residentesBaseList.stream()
                                .filter(r -> r.getFechaNacimiento().getMonthValue() == filtre.getMonth())
                                .toList();
                    }
                }
            }
        }

        return residentesBaseList.stream().map(ResidenteResponseDto::new).collect(Collectors.toList());
    }


    /**
     * Elimina un residente asegurando su pertenencia a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @throws ApiException si no existe el residente o no pertenece a la residencia.
     */
    public void delete(Long idResidencia, Long idResidente) {
        Residente residenteTmp = getResidente(idResidencia, idResidente);
        // Eliminar el residente
        residenteRepository.delete(residenteTmp);

    }

    /**
     * Actualiza parcialmente los datos de un residente.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @param input DTO con los nuevos datos a actualizar.
     * @return DTO actualizado del residente.
     * @throws ApiException si hay errores de validación o duplicidad de documento.
     */
    public ResidenteResponseDto update(Long idResidencia, Long idResidente, ResidenteDto input) {
        if (idResidencia == null || idResidente == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que el residente existe
        Residente residenteUpdatable = getResidente(idResidencia, idResidente);

        //Si ha añadido algun campo en el input para actualizar
        if (input != null){
            // Validar el formato del documento de identidad
            if (input.getDocumentoIdentidad() != null){
                input.setDocumentoIdentidad(input.getDocumentoIdentidad().trim().toUpperCase());
                if (input.getDocumentoIdentidad().length() != 8){
                    throw new ApiException(ApiErrorCode.DOCUMENTO_INVALIDO);
                }

                // Comprobar si ya existe otro residente con el mismo documento de identidad
                Residente residenteDup = residenteRepository.findByDocuemntoIdentidad(input.getDocumentoIdentidad());
                if (residenteDup != null){
                    if (!Objects.equals(residenteDup.getId(), idResidente)){
                        throw new ApiException(ApiErrorCode.DOCUMENTO_DUPLICADO);
                    }

                }else
                    residenteUpdatable.setDocuemntoIdentidad(input.getDocumentoIdentidad());
            }
            if (input.getFechaNacimiento() != null){
                // Validar que la fecha de nacimiento no sea futura
                if(input.getFechaNacimiento().isAfter(LocalDate.now())){
                    throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
                }
                residenteUpdatable.setFechaNacimiento(input.getFechaNacimiento());
            }
            if (input.getNombre() != null && !input.getNombre().trim().isEmpty()){
                residenteUpdatable.setNombre(input.getNombre());
            }
            if (input.getApellido() != null && !input.getApellido().trim().isEmpty()){
                residenteUpdatable.setApellido(input.getApellido());
            }
            // Guardar los cambios
            residenteUpdatable = residenteRepository.save(residenteUpdatable);
        }
        return new ResidenteResponseDto(residenteUpdatable);
    }

    /**
     * Obtiene un residente por ID validando su pertenencia a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @return El residente encontrado.
     * @throws ApiException si no existe o no pertenece a la residencia.
     */
    private Residente getResidente(Long idResidencia, Long idResidente){
        if (idResidencia == null || idResidente == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que el residente existe
        Residente residenteTmp = residenteRepository.findById(idResidente).orElse(null);
        if (residenteTmp == null){
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }
        //Comrpobar que el residente pertenece a la residencia
        if (!Objects.equals(residenteTmp.getResidencia().getId(), idResidencia)){
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }
        return residenteTmp;
    }

}
