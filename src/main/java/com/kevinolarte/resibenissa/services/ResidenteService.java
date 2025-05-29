package com.kevinolarte.resibenissa.services;


import com.kevinolarte.resibenissa.config.Conf;
import com.kevinolarte.resibenissa.dto.in.ResidenteDto;
import com.kevinolarte.resibenissa.dto.in.moduloReporting.EmailRequestDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
import com.kevinolarte.resibenissa.enums.Filtrado.ResidenteFiltrado;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.repositories.ResidenteRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import com.kevinolarte.resibenissa.specifications.ResidenteSpecification;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @throws ResiException en caso de datos inválidos o duplicidad de documento.
     */
    public ResidenteResponseDto add(Long idResidencia, ResidenteDto input) throws ResiException {
        if (input.getNombre() == null || input.getApellido() == null || input.getFechaNacimiento() == null || input.getDocumentoIdentidad() == null ||
                input.getNombre().trim().isEmpty() || input.getApellido().trim().isEmpty() || input.getDocumentoIdentidad().trim().isEmpty() || idResidencia == null ||
            input.getFamiliar1() == null || input.getFamiliar1().trim().isEmpty()) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar el formato del documento de identidad
        input.setDocumentoIdentidad(input.getDocumentoIdentidad().trim().toUpperCase());
        if (input.getDocumentoIdentidad().length() != 8) {
            throw new ResiException(ApiErrorCode.DOCUMENTO_INVALIDO);
        }

        //Validar correo familiar 1
        if (!EmailService.isEmailValid(input.getFamiliar1().toLowerCase().trim())){
            throw new ResiException(ApiErrorCode.CORREO_INVALIDO);
        }
        //Validar correo familiar 2
        if (input.getFamiliar2() != null && !input.getFamiliar2().trim().isEmpty())
            if (!EmailService.isEmailValid(input.getFamiliar2().toLowerCase().trim()))
                throw new ResiException(ApiErrorCode.CORREO_INVALIDO);



        // Validar que la fecha de nacimiento no sea futura
        if (input.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new ResiException(ApiErrorCode.FECHA_INVALIDO);
        }

        // Validar que la residencia existe
        Residencia residencia = residenciaService.getResidencia(idResidencia);

        // Validar que no existe otro residente con el mismo documento de identidad en cualquier residencia
        Residente residenteDup = residenteRepository.findByDocuemntoIdentidad(input.getDocumentoIdentidad());
        if (residenteDup != null) {
            throw new ResiException(ApiErrorCode.DOCUMENTO_DUPLICADO);
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
     * @throws ResiException si el residente no existe o no pertenece a la residencia.
     */
    public ResidenteResponseDto get(Long idResidencia, Long idResidente) {

        Residente residenteTmp = getResidente(idResidencia, idResidente);

        return new ResidenteResponseDto(residenteTmp);


    }

    /**
     * Obtiene todos los residentes de todas residencias con filtros opcionales.
     * @param fechaNacimiento   Fecha exacta de nacimiento (opcional).
     * @param minFNac           Fecha mínima de nacimiento (opcional).
     * @param maxFNac           Fecha máxima de nacimiento (opcional).
     * @param maxAge            Edad máxima (opcional).
     * @param minAge            Edad mínima (opcional).
     * @param idJuego           ID de juego asociado (opcional).
     * @param idEvento          ID de evento asociado (opcional).
     * @return Lista de residentes filtrados.
     * @throws ResiException si la residencia no existe.
     */
    public List<ResidenteResponseDto> getAll(LocalDate fechaNacimiento, LocalDate minFNac, LocalDate maxFNac, Integer maxAge, Integer minAge, Long idJuego, Long idEvento, ResidenteFiltrado filtrado) {



        Specification<Residente> spec = ResidenteSpecification.withFilters(null, fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento);

        Sort sort = (filtrado != null) ? filtrado.toSort() : Sort.by(Sort.Direction.ASC, "apellido");
        List<Residente> residentes = residenteRepository.findAll(spec, sort);


        return residentes.stream().map(ResidenteResponseDto::new).collect(Collectors.toList());

    }

    /**
     * Obtiene todos los residentes de una residencia con filtros opcionales.
     *
     * @param idResidencia      ID de la residencia.
     * @param fechaNacimiento   Fecha exacta de nacimiento (opcional).
     * @param minFNac           Fecha mínima de nacimiento (opcional).
     * @param maxFNac           Fecha máxima de nacimiento (opcional).
     * @param maxAge            Edad máxima (opcional).
     * @param minAge            Edad mínima (opcional).
     * @param idJuego           ID de juego asociado (opcional).
     * @param idEvento          ID de evento asociado (opcional).
     * @return Lista de residentes filtrados.
     * @throws ResiException si la residencia no existe o el ID es nulo.
     */
    public List<ResidenteResponseDto> getAll(Long idResidencia, LocalDate fechaNacimiento, LocalDate minFNac, LocalDate maxFNac, Integer maxAge, Integer minAge, Long idJuego, Long idEvento,  ResidenteFiltrado filtrado) {
        if (idResidencia == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que la residencia existe
        Residencia residencia = residenciaService.getResidencia(idResidencia);

        Specification<Residente> spec = ResidenteSpecification.withFilters(idResidencia, fechaNacimiento, minFNac, maxFNac, maxAge, minAge, idJuego, idEvento);

        Sort sort = (filtrado != null) ? filtrado.toSort() : Sort.by(Sort.Direction.ASC, "apellido");
        List<Residente> residentes = residenteRepository.findAll(spec, sort);


        return residentes.stream().map(ResidenteResponseDto::new).collect(Collectors.toList());
    }

    /**
     * Obtiene todos los residentes dados de baja en una residencia, con filtros de fecha.
     * @param idResidencia ID de la residencia.
     * @param fecha Fecha exacta de baja (opcional).
     * @param minFecha Fecha mínima de baja (opcional).
     * @param maxFecha Fecha máxima de baja (opcional).
     * @return Lista de residentes dados de baja.
     * @throws ResiException si la residencia no existe o el ID es nulo.
     */
    public List<ResidenteResponseDto> getAllBajas(Long idResidencia, LocalDate fecha, LocalDate minFecha, LocalDate maxFecha) {
        if (idResidencia == null){
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        List<Residente> lista =  residenteRepository.findAll(ResidenteSpecification.withFiltersBaja(fecha, minFecha, maxFecha, idResidencia));

        return lista.stream().map(ResidenteResponseDto::new).toList();
    }

    /**
     * Obtiene todos los residentes dados de baja en el sistema, sin filtrar por residencia.
     * @param fecha Fecha exacta de baja (opcional).
     * @param minFecha Fecha mínima de baja (opcional).
     * @param maxFecha Fecha máxima de baja (opcional).
     * @return Lista de residentes dados de baja.
     * @throws ResiException si la residencia no existe o el ID es nulo.
     */
    public List<ResidenteResponseDto> getAllBajas( LocalDate fecha, LocalDate minFecha, LocalDate maxFecha) {
        List<Residente> lista =  residenteRepository.findAll(ResidenteSpecification.withFiltersBaja(fecha, minFecha, maxFecha, null));

        return lista.stream().map(ResidenteResponseDto::new).toList();
    }




    /**
     * Elimina un residente asegurando su pertenencia a una residencia.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     * @throws ResiException si no existe el residente o no pertenece a la residencia.
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
     * @throws ResiException si el residente no existe o ya está dado de baja.
     */
    public void deleteLogico(Long idResidencia, Long idResidente) {
        // Validar que el residente existe
        Residente residenteUpdatable = getResidente(idResidencia, idResidente);

        //Dar de baja al residente
        if (residenteUpdatable.isBaja())
            throw new ResiException(ApiErrorCode.RESIDENTE_BAJA);

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
     * @throws ResiException si los datos son inválidos o se detecta duplicidad de documento.
     */
    public ResidenteResponseDto update(Long idResidencia, Long idResidente, ResidenteDto input) {
        if (idResidencia == null || idResidente == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        // Validar que el residente existe
        Residente residenteUpdatable = getResidente(idResidencia, idResidente);

        //Comprobar si el residente ya se ha dado de baja
        if (residenteUpdatable.isBaja()) {
            throw new ResiException(ApiErrorCode.RESIDENTE_BAJA);
        }

        //Si ha añadido algun campo en el input para actualizar
        if (input != null) {
            // Validar el formato del documento de identidad
            if (input.getDocumentoIdentidad() != null) {
                input.setDocumentoIdentidad(input.getDocumentoIdentidad().trim().toUpperCase());
                if (input.getDocumentoIdentidad().length() != 8) {
                    throw new ResiException(ApiErrorCode.DOCUMENTO_INVALIDO);
                }

                // Comprobar si ya existe otro residente con el mismo documento de identidad
                Residente residenteDup = residenteRepository.findByDocuemntoIdentidad(input.getDocumentoIdentidad());
                if (residenteDup != null) {
                    if (!Objects.equals(residenteDup.getId(), idResidente)) {
                        throw new ResiException(ApiErrorCode.DOCUMENTO_DUPLICADO);
                    }

                } else
                    residenteUpdatable.setDocuemntoIdentidad(input.getDocumentoIdentidad());
            }
            if (input.getFechaNacimiento() != null) {
                // Validar que la fecha de nacimiento no sea futura
                if (input.getFechaNacimiento().isAfter(LocalDate.now())) {
                    throw new ResiException(ApiErrorCode.FECHA_INVALIDO);
                }
                residenteUpdatable.setFechaNacimiento(input.getFechaNacimiento());
            }
            if (input.getNombre() != null && !input.getNombre().trim().isEmpty()) {
                residenteUpdatable.setNombre(input.getNombre());
            }
            if (input.getApellido() != null && !input.getApellido().trim().isEmpty()) {
                residenteUpdatable.setApellido(input.getApellido());
            }
            if (input.getFamiliar1() != null && !input.getFamiliar1().trim().isEmpty()) {
                //Validar correo familiar 1
                if (!EmailService.isEmailValid(input.getFamiliar1().toLowerCase().trim())) {
                    throw new ResiException(ApiErrorCode.CORREO_INVALIDO);
                }
                residenteUpdatable.setFamiliar1(input.getFamiliar1());
            }
            if (input.getFamiliar2() != null && !input.getFamiliar2().trim().isEmpty()) {
                //Validar correo familiar 2
                if (!EmailService.isEmailValid(input.getFamiliar2().toLowerCase().trim())) {
                    throw new ResiException(ApiErrorCode.CORREO_INVALIDO);
                }
                residenteUpdatable.setFamiliar2(input.getFamiliar2());
            }
            // Guardar los cambios
            residenteUpdatable = residenteRepository.save(residenteUpdatable);
        }
        return new ResidenteResponseDto(residenteUpdatable);
    }








    /**
     * Envía un correo electrónico a los familiares de un residente.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     * @param input        DTO con el asunto y cuerpo del correo.
     * @throws ResiException si el residente no existe o no tiene familiares asociados.
     */
    public void sendEmailFamiliar(Long idResidencia, Long idResidente, EmailRequestDto input) {

        // Validar que el residente existe
        Residente residenteTmp = getResidente(idResidencia, idResidente);

        if (input == null || input.getSubject() == null || input.getBody() == null ||
                input.getSubject().trim().isEmpty() || input.getBody().trim().isEmpty()) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Enviar correo al familiar
        try{
            emailService.sendEmail(residenteTmp.getFamiliar1(), input.getSubject(), input.getBody());
            if(residenteTmp.getFamiliar2() != null)
                emailService.sendEmail(residenteTmp.getFamiliar2(), input.getSubject(), input.getBody());
        }catch (Exception e){
            throw new ResiException(ApiErrorCode.ERROR_MAIL_SENDER);
        }
    }




    /**
     * Marca un residente como dado de baja.
     * @param residenteUpdatable Residente a actualizar.
     * @param passwordEncoder Codificador de contraseñas.
     *
     */
    public static void darBajaUser(Residente residenteUpdatable, PasswordEncoder passwordEncoder) {
        residenteUpdatable.setBaja(true);
        residenteUpdatable.setFechaBaja(LocalDateTime.now());
        residenteUpdatable.setDocuemntoIdentidad(passwordEncoder.encode(residenteUpdatable.getDocuemntoIdentidad()));
        residenteUpdatable.setFamiliar1(passwordEncoder.encode(residenteUpdatable.getFamiliar1()));
        if (residenteUpdatable.getFamiliar2() != null && !residenteUpdatable.getFamiliar2().trim().isEmpty())
            residenteUpdatable.setFamiliar2(passwordEncoder.encode(residenteUpdatable.getFamiliar2()));
    }




    /**
     * Obtiene un residente y valida que pertenece a la residencia especificada.
     *
     * @param idResidencia ID de la residencia.
     * @param idResidente  ID del residente.
     * @return Residente encontrado.
     * @throws ResiException si no existe o no pertenece a la residencia.
     */
    public Residente getResidente(Long idResidencia, Long idResidente) {
        if (idResidencia == null || idResidente == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar que el residente existe
        Residente residenteTmp = residenteRepository.findById(idResidente).orElse(null);
        if (residenteTmp == null) {
            throw new ResiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }
        //Comrpobar que el residente pertenece a la residencia
        if (!Objects.equals(residenteTmp.getResidencia().getId(), idResidencia)) {
            throw new ResiException(ApiErrorCode.RESIDENTE_INVALIDO);
        }
        return residenteTmp;
    }


    /**
     * Obtiene una imagen como recurso desde el sistema de archivos.
     * @param filename Nombre del archivo solicitado (actualmente no se utiliza, se carga siempre la imagen por defecto).
     * @return {@link Resource} que representa la imagen cargada desde el sistema de archivos.
     * @throws ResiException si el archivo no existe o no puede accederse.
     */
    public Resource getImage(String filename) {

        Path filePath = Paths.get("src/main/resources/static/uploads").resolve(Conf.imageDefault).normalize();
        Resource resource;
        try{
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ResiException(ApiErrorCode.PROBLEMAS_CON_FILE);
            }
        }catch (Exception e){
            throw new ResiException(ApiErrorCode.PROBLEMAS_CON_FILE);
        }
        return resource;
    }
}
