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
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import com.kevinolarte.resibenissa.specifications.ResidenteSpecification;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los residentes.
 * <p>
 * Permite registrar, consultar, actualizar y eliminar residentes asociados a residencias.
 * También permite aplicar filtros básicos sobre los residentes.
 * </p>
 *
 * @author : Kevin Olarte
 */
@Service
@AllArgsConstructor
public class ResidenteService {
    private final ResidenteRepository residenteRepository;
    private final ResidenciaService residenciaService;
    private final PasswordEncoder passwordEncoder;
    private final ParticipanteRepository participanteRepository;
    private final EmailService emailService;

    /**
     * Registra un nuevo residente asociado a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param input        DTO con los datos del residente.
     * @return DTO del residente creado.
     * @throws ApiException en caso de datos inválidos o duplicidad de documento.
     */
    public ResidenteResponseDto add(Long idResidencia, ResidenteDto input) throws ApiException {
        if (input.getNombre() == null || input.getApellido() == null || input.getFechaNacimiento() == null || input.getDocumentoIdentidad() == null ||
                input.getNombre().trim().isEmpty() || input.getApellido().trim().isEmpty() || input.getDocumentoIdentidad().trim().isEmpty() || idResidencia == null ||
            input.getFamiliar1() == null || input.getFamiliar1().trim().isEmpty()) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar el formato del documento de identidad
        input.setDocumentoIdentidad(input.getDocumentoIdentidad().trim().toUpperCase());
        if (input.getDocumentoIdentidad().length() != 8) {
            throw new ApiException(ApiErrorCode.DOCUMENTO_INVALIDO);
        }

        //Validar correo familiar 1
        if (!EmailService.isEmailValid(input.getFamiliar1().toLowerCase().trim())){
            throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
        }
        //Validar correo familiar 2
        if (input.getFamiliar2() != null && !input.getFamiliar2().trim().isEmpty())
            if (!EmailService.isEmailValid(input.getFamiliar2().toLowerCase().trim()))
                throw new ApiException(ApiErrorCode.CORREO_INVALIDO);



        // Validar que la fecha de nacimiento no sea futura
        if (input.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
        }

        // Validar que la residencia existe
        Residencia residencia = residenciaService.findById(idResidencia);
        if (residencia == null) {
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        // Validar que no existe otro residente con el mismo documento de identidad en cualquier residencia
        Residente residenteDup = residenteRepository.findByDocuemntoIdentidad(input.getDocumentoIdentidad());
        if (residenteDup != null) {
            throw new ApiException(ApiErrorCode.DOCUMENTO_DUPLICADO);
        }

        // Crear el nuevo residente
        Residente residente = new Residente(input.getNombre(), input.getApellido(), input.getFechaNacimiento(), input.getDocumentoIdentidad(), input.getFamiliar1(), input.getFamiliar2());
        residente.setResidencia(residencia);
        Residente residenteSaved = residenteRepository.save(residente);
        return new ResidenteResponseDto(residenteSaved);

    }



    /**
     * Obtiene un residente asegurando su pertenencia a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     * @return DTO del residente encontrado.
     * @throws ApiException si el residente no existe o no pertenece a la residencia.
     */
    public ResidenteResponseDto get(Long idResidencia, Long idResidente) {

        Residente residenteTmp = getResidente(idResidencia, idResidente);

        return new ResidenteResponseDto(residenteTmp);


    }


    public List<ResidenteResponseDto> getAll(LocalDate fechaNacimiento, LocalDate minFNac, LocalDate maxFNac, Integer maxAge, Integer minAge, Long idJuego, Long idEvento) {

        List<Residente> residentesBaseList = residenteRepository.findAll(ResidenteSpecification.withFilters(null, fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento));


        return residentesBaseList.stream().map(ResidenteResponseDto::new).collect(Collectors.toList());

    }

    public List<ResidenteResponseDto> getAll(Long idResidencia, LocalDate fechaNacimiento, LocalDate minFNac, LocalDate maxFNac, Integer maxAge, Integer minAge, Long idJuego, Long idEvento) {
        if (idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que la residencia existe
        Residencia residencia = residenciaService.findById(idResidencia);
        // Comprobar que la residencia existe
        if (residencia == null) {
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        // Obtener todos los residentes de la residencia
        List<Residente> residentesBaseList = residenteRepository.findAll(ResidenteSpecification.withFilters(idResidencia, fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento));


        return residentesBaseList.stream().map(ResidenteResponseDto::new).collect(Collectors.toList());
    }


    /**
     * Elimina un residente asegurando su pertenencia a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     * @throws ApiException si no existe el residente o no pertenece a la residencia.
     */
    public void deleteFisico(Long idResidencia, Long idResidente) {
        Residente residenteTmp = getResidente(idResidencia, idResidente);
        // Eliminar el residente
        residenteRepository.delete(residenteTmp);

    }

    /**
     * Elimina un residente de forma lógica, marcándolo como dado de baja.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     */
    public void deleteLogico(Long idResidencia, Long idResidente) {
        // Validar que el residente existe
        Residente residenteUpdatable = getResidente(idResidencia, idResidente);

        //Dar de baja al residente
        if (residenteUpdatable.isBaja())
            throw new ApiException(ApiErrorCode.RESIDENTE_BAJA);

        darBajaUser(residenteUpdatable, passwordEncoder);
        participanteRepository.deleteAll(residenteUpdatable.getParticipantes());
        residenteRepository.save(residenteUpdatable);


    }


    /**
     * Actualiza los datos de un residente.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     * @param input        DTO con los nuevos datos a actualizar.
     * @return DTO del residente actualizado.
     * @throws ApiException si los datos son inválidos o se detecta duplicidad de documento.
     */
    public ResidenteResponseDto update(Long idResidencia, Long idResidente, ResidenteDto input) {
        if (idResidencia == null || idResidente == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que el residente existe
        Residente residenteUpdatable = getResidente(idResidencia, idResidente);

        //Comprobar si el residente ya se ha dado de baja
        if (residenteUpdatable.isBaja()) {
            throw new ApiException(ApiErrorCode.RESIDENTE_BAJA);
        }

        //Si ha añadido algun campo en el input para actualizar
        if (input != null) {
            // Validar el formato del documento de identidad
            if (input.getDocumentoIdentidad() != null) {
                input.setDocumentoIdentidad(input.getDocumentoIdentidad().trim().toUpperCase());
                if (input.getDocumentoIdentidad().length() != 8) {
                    throw new ApiException(ApiErrorCode.DOCUMENTO_INVALIDO);
                }

                // Comprobar si ya existe otro residente con el mismo documento de identidad
                Residente residenteDup = residenteRepository.findByDocuemntoIdentidad(input.getDocumentoIdentidad());
                if (residenteDup != null) {
                    if (!Objects.equals(residenteDup.getId(), idResidente)) {
                        throw new ApiException(ApiErrorCode.DOCUMENTO_DUPLICADO);
                    }

                } else
                    residenteUpdatable.setDocuemntoIdentidad(input.getDocumentoIdentidad());
            }
            if (input.getFechaNacimiento() != null) {
                // Validar que la fecha de nacimiento no sea futura
                if (input.getFechaNacimiento().isAfter(LocalDate.now())) {
                    throw new ApiException(ApiErrorCode.FECHA_INVALIDO);
                }
                residenteUpdatable.setFechaNacimiento(input.getFechaNacimiento());
            }
            if (input.getNombre() != null && !input.getNombre().trim().isEmpty()) {
                residenteUpdatable.setNombre(input.getNombre());
            }
            if (input.getApellido() != null && !input.getApellido().trim().isEmpty()) {
                residenteUpdatable.setApellido(input.getApellido());
            }
            // Guardar los cambios
            residenteUpdatable = residenteRepository.save(residenteUpdatable);
        }
        return new ResidenteResponseDto(residenteUpdatable);
    }





    /**
     * Obtiene un residente y valida que pertenece a la residencia especificada.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     * @return Residente encontrado.
     * @throws ApiException si no existe o no pertenece a la residencia.
     */
    public Residente getResidente(Long idResidencia, Long idResidente) {
        if (idResidencia == null || idResidente == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que el residente existe
        Residente residenteTmp = residenteRepository.findById(idResidente).orElse(null);
        if (residenteTmp == null) {
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }
        //Comrpobar que el residente pertenece a la residencia
        if (!Objects.equals(residenteTmp.getResidencia().getId(), idResidencia)) {
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }
        return residenteTmp;
    }

    public List<ResidenteResponseDto> getAllBajas(Long idResidencia, LocalDate fecha, LocalDate minFecha, LocalDate maxFecha) {
        if (idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        List<Residente> lista =  residenteRepository.findAll(ResidenteSpecification.withFiltersBaja(fecha, minFecha, maxFecha, idResidencia));

        return lista.stream().map(ResidenteResponseDto::new).toList();
    }

    public List<ResidenteResponseDto> getAllBajas( LocalDate fecha, LocalDate minFecha, LocalDate maxFecha) {
        List<Residente> lista =  residenteRepository.findAll(ResidenteSpecification.withFiltersBaja(fecha, minFecha, maxFecha, null));

        return lista.stream().map(ResidenteResponseDto::new).toList();
    }

    static void darBajaUser(Residente residenteUpdatable, PasswordEncoder passwordEncoder) {
        residenteUpdatable.setBaja(true);
        residenteUpdatable.setFechaBaja(LocalDateTime.now());
        residenteUpdatable.setDocuemntoIdentidad(passwordEncoder.encode(residenteUpdatable.getDocuemntoIdentidad()));
        residenteUpdatable.setFamiliar1(passwordEncoder.encode(residenteUpdatable.getFamiliar1()));
        if (residenteUpdatable.getFamiliar2() != null && !residenteUpdatable.getFamiliar2().trim().isEmpty())
            residenteUpdatable.setFamiliar2(passwordEncoder.encode(residenteUpdatable.getFamiliar2()));
    }

}
