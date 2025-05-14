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

    /**
     * Registra un nuevo residente asociado a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param input DTO con los datos del residente.
     * @return DTO del residente creado.
     * @throws ApiException en caso de datos inválidos o duplicidad de documento.
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
     * @param id ID del residente.
     * @return El residente encontrado o {@code null} si no existe.
     */
    public Residente findById(long id) {

        return residenteRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene un residente asegurando su pertenencia a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @return DTO del residente encontrado.
     * @throws ApiException si el residente no existe o no pertenece a la residencia.
     */
    public ResidenteResponseDto get(Long idResidencia, Long idResidente){

        Residente residenteTmp = getResidente(idResidencia, idResidente);

        return new ResidenteResponseDto(residenteTmp);


    }

    /**
     * Obtiene todos los residentes de una residencia, aplicando filtros opcionales.
     * <p>
     *     * Permite filtrar por documento de identidad, fecha de nacimiento, año o mes de nacimiento.
     *     * Si no se aplica ningún filtro, se devuelven todos los residentes de la residencia.
     *     </p>
     * @param idResidencia ID de la residencia.
     * @param fechaNacimiento Fecha de nacimiento del residente (opcional).
     * @param year Año de nacimiento del residente (opcional).
     * @param month Mes de nacimiento del residente (opcional).
     * @param documentoIdentidad Documento de identidad del residente (opcional).
     * @return Lista de residentes filtrados.
     * @throws ApiException si el ID de residencia es nulo o la residencia no existe.
     */
    public List<ResidenteResponseDto> getAll(Long idResidencia, LocalDate fechaNacimiento, Integer year, Integer month, Integer maxAge, Integer minAge, String documentoIdentidad, Long idJuego, Long idEventoSalida, Long minRegistro, Long maxRegistro) {
        if (idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que la residencia existe
        Residencia residencia = residenciaService.findById(idResidencia);
        // Comprobar que la residencia existe
        if(residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }
        if (minAge != null && maxAge != null){
            if (minAge > maxAge){
                throw new ApiException(ApiErrorCode.RANGO_EDAD_INVALIDO);
            }
        }

        // Obtener todos los residentes de la residencia
        List<Residente> residentesBaseList =  residenteRepository.findByResidenciaAndBajaFalse(residencia);


        residentesBaseList = residentesBaseList.stream()
                .filter(residente -> {
                    boolean match = true;
                    if (documentoIdentidad != null) match = residente.getDocuemntoIdentidad().equalsIgnoreCase(documentoIdentidad);
                    if (fechaNacimiento != null) match = residente.getFechaNacimiento().isEqual(fechaNacimiento);
                    else{
                        if (year != null) match = residente.getFechaNacimiento().getYear() == year;
                        if (month != null) match = residente.getFechaNacimiento().getMonthValue() == month;
                    }
                    if (maxAge != null) match = residente.getFechaNacimiento().plusYears(maxAge).isAfter(LocalDate.now());
                    if (minAge != null) match = residente.getFechaNacimiento().plusYears(minAge).isBefore(LocalDate.now());
                    if (idJuego != null)match = residente.getRegistros().stream()
                                            .anyMatch(registroJuego -> registroJuego.getJuego().getId().equals(idJuego));
                    if (idEventoSalida != null) match = residente.getParticipantes().stream().
                            anyMatch(participante -> participante.getSalida().getId().equals(idEventoSalida));

                    if (minRegistro != null && maxRegistro != null) match = residente.getRegistros().size() >= minRegistro && residente.getRegistros().size() <= maxRegistro;
                    else if(minRegistro != null) match = residente.getRegistros().size() >= minRegistro;
                    else if(maxRegistro != null) match = residente.getRegistros().size() <= maxRegistro;

                    return match;
                }).toList();

        return residentesBaseList.stream().map(ResidenteResponseDto::new).collect(Collectors.toList());
    }


    /**
     * Elimina un residente asegurando su pertenencia a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @throws ApiException si no existe el residente o no pertenece a la residencia.
     */
    public void deleteFisico(Long idResidencia, Long idResidente) {
        Residente residenteTmp = getResidente(idResidencia, idResidente);
        // Eliminar el residente
        residenteRepository.delete(residenteTmp);

    }

    /**
     * Elimina un residente de forma lógica, marcándolo como dado de baja.
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     */
    public void deleteLogico(Long idResidencia, Long idResidente) {
        // Validar que el residente existe
        Residente residenteUpdatable = getResidente(idResidencia, idResidente);

        //Dar de baja al residente
        if (residenteUpdatable.isBaja())
            throw new ApiException(ApiErrorCode.RESIDENTE_BAJA);

        residenteUpdatable.setBaja(true);
        residenteUpdatable.setFechaBaja(LocalDateTime.now());
        residenteUpdatable.setDocuemntoIdentidad(passwordEncoder.encode(residenteUpdatable.getDocuemntoIdentidad()));
        participanteRepository.deleteAll(residenteUpdatable.getParticipantes());
        residenteRepository.save(residenteUpdatable);


    }

    /**
     * Actualiza los datos de un residente.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @param input DTO con los nuevos datos a actualizar.
     * @return DTO del residente actualizado.
     * @throws ApiException si los datos son inválidos o se detecta duplicidad de documento.
     */
    public ResidenteResponseDto update(Long idResidencia, Long idResidente, ResidenteDto input) {
        if (idResidencia == null || idResidente == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que el residente existe
        Residente residenteUpdatable = getResidente(idResidencia, idResidente);

        //Comprobar si el residente ya se ha dado de baja
        if (residenteUpdatable.isBaja()){
            throw new ApiException(ApiErrorCode.RESIDENTE_BAJA);
        }

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
     * Obtiene un residente y valida que pertenece a la residencia especificada.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente ID del residente.
     * @return Residente encontrado.
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


    /**
     * Obtiene todos los residentes dados de baja de una residencia.
     * @param idResidencia ID de la residencia.
     * @return Lista de residentes dados de baja.
     * @throws ApiException si el ID de residencia es nulo o la residencia no existe.
     */
    public List<ResidenteResponseDto> getAllBajas(Long idResidencia, String documentoIdentidad) {
        if (idResidencia == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que la residencia existe
        Residencia residencia = residenciaService.findById(idResidencia);
        if (residencia == null){
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }
        // Obtener todos los residentes de la residencia
        List<Residente> residentesBaseList =  residenteRepository.findByResidenciaAndBajaTrue(residencia);

        if(documentoIdentidad != null && !documentoIdentidad.trim().isEmpty()){
            // Filtrar por documento de identidad
            residentesBaseList = residentesBaseList.stream()
                    .filter(residente -> residente.getDocuemntoIdentidad().equalsIgnoreCase(documentoIdentidad))
                    .toList();
        }
        return residentesBaseList.stream().map(ResidenteResponseDto::new).toList();
    }
}
