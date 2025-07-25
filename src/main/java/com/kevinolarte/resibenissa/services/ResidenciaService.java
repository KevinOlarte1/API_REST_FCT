package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.in.ResidenciaDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaPublicResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenciaResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import com.kevinolarte.resibenissa.repositories.ResidenteRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.EventoSalidaRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final ResidenteRepository residenteRepository;
    private final ParticipanteRepository participanteRepository;
    private final EventoSalidaRepository eventoSalidaRepository;


    /**
     * Crea una nueva residencia en el sistema a partir de los datos recibidos.
     * <p>
     * Valida que el nombre y correo no estén vacíos, que el correo tenga un formato válido,
     * y que tanto el nombre como el correo no estén ya registrados.
     * </p>
     *
     * @param input DTO que contiene el nombre y correo de la residencia.
     * @return {@link ResidenciaResponseDto} de la residencia creada.
     * @throws ResiException en caso de errores de validación o duplicados.
     */
    public ResidenciaResponseDto add(ResidenciaDto input) throws RuntimeException{
        if (input.getNombre() == null || input.getEmail() == null
                || input.getNombre().trim().isEmpty() || input.getEmail().trim().isEmpty()){
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        // Validar formato del correo electrónico
        input.setEmail(input.getEmail().toLowerCase().trim());
        if (!EmailService.isEmailValid(input.getEmail())){
            throw new ResiException(ApiErrorCode.CORREO_INVALIDO);
        }

        // Comprobar si ya existe una residencia con ese correo o nombre
        Optional<Residencia> residenciaTmp = residenciaRepository.findByEmail(input.getEmail());
        Optional<Residencia> residenciaTmp2 = residenciaRepository.findByNombre(input.getNombre());
        if(residenciaTmp.isPresent()){
            throw new ResiException(ApiErrorCode.CORREO_DUPLICADO);
        }
        if(residenciaTmp2.isPresent()){
            throw new ResiException(ApiErrorCode.NOMBRE_DUPLICADO);
        }

        Residencia residencia = new Residencia(input.getNombre(), input.getEmail());
        return new ResidenciaResponseDto(residenciaRepository.save(residencia));
    }




    /**
     * Obtiene una residencia a partir de su ID validando su existencia.
     *
     * @param idResidencia ID de la residencia a recuperar.
     * @return {@link ResidenciaResponseDto} de la residencia encontrada.
     * @throws ResiException si el ID es nulo o no existe una residencia con ese ID.
     */
    public ResidenciaResponseDto get(Long idResidencia) {
        Residencia resi;

        // Comprobar si el ID de residencia es nulo
        if (idResidencia == null)
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        else
            resi = residenciaRepository.findById(idResidencia)
                    .orElseThrow(() -> new ResiException(ApiErrorCode.RESIDENCIA_INVALIDO));


        return new ResidenciaResponseDto(resi);
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

    /**
     * Obtiene una lista de todas las residencias que están marcadas como inactivas (baja).
     * @return Lista de residencias inactivas.
     */
    public List<ResidenciaPublicResponseDto> getAllBaja() {
        return residenciaRepository.findByBajaTrue().stream().map(ResidenciaPublicResponseDto::new).toList();
    }




    /**
     * Elimina una residencia a partir de su ID.
     * <p>
     * Si no se encuentra la residencia, lanza una excepción.
     * </p>
     *
     * @param idResidencia ID de la residencia a eliminar.
     * @throws ResiException si no se encuentra la residencia especificada.
     */
    public void deleteFisico(Long idResidencia) {
        Residencia residenciaTmp = residenciaRepository.findById(idResidencia).orElse(null);

        if(residenciaTmp == null){
            throw new ResiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        residenciaRepository.delete(residenciaTmp);
    }

    /**
     * Elimina una residencia de forma lógica, marcando su estado como inactivo.
     * @param id ID de la residencia a eliminar.
     * @throws ResiException si no se encuentra la residencia.
     */
    public void deleteLogico(Long id) {
        Residencia residencia = residenciaRepository.findById(id).orElse(null);
        if (residencia == null) {
            throw new ResiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        residencia.setBaja(true);
        residencia.setFechaBaja(LocalDateTime.now());
        residencia.setEmail(passwordEncoder.encode(residencia.getEmail()));

        // Cambiar el estado de los residentes a inactivos
        residencia.getResidentes().forEach(residente -> {
            ResidenteService.darBajaUser(residente, passwordEncoder);
            residenteRepository.save(residente);
        });
        // Cambiar el estado de los usuarios a inactivos
        residencia.getUsuarios().forEach(usuario -> {
            usuario.setBaja(true);
            usuario.setFechaBaja(LocalDateTime.now());
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        });

        participanteRepository.deleteAllByResidenciaId(residencia.getId());
        eventoSalidaRepository.deleteAllByResidenciaId(residencia.getId());



        residenciaRepository.save(residencia);
    }








    /**
     * Busca una residencia por su ID.
     *
     * @param id ID de la residencia.
     * @return {@link Residencia} encontrada.
     * @throws ResiException si no se encuentra la residencia.
     */
    public Residencia getResidencia(Long id){
        return residenciaRepository.findById(id).orElseThrow(() -> new ResiException(ApiErrorCode.RESIDENCIA_INVALIDO));
    }

}

