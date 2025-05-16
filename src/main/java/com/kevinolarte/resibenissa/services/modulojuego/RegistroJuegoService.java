package com.kevinolarte.resibenissa.services.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import com.kevinolarte.resibenissa.repositories.modulojuego.RegistroJuegoRepository;

import com.kevinolarte.resibenissa.services.ResidenciaService;
import com.kevinolarte.resibenissa.services.ResidenteService;
import com.kevinolarte.resibenissa.services.UserService;
import com.kevinolarte.resibenissa.specifications.RegistroJuegoSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar los registros de juegos jugados por los residentes.
 * <p>
 * Proporciona métodos para agregar, consultar, actualizar y eliminar registros de juegos.
 * </p>
 *
 * Las operaciones incluyen validaciones de consistencia entre entidades relacionadas
 * (residente, juego, usuario) y controles de integridad de datos.
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class RegistroJuegoService {

    private final RegistroJuegoRepository registroJuegoRepository;
    private final ResidenteService residenteService;
    private final JuegoService juegoService;
    private final UserService userService;
    private final ResidenciaService residenciaService;
    private final ResidenciaRepository residenciaRepository;


    /**
     * Crea un nuevo registro de juego para un residente en una residencia y juego específicos.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param input Datos del registro de juego.
     * @return El registro de juego creado.
     * @throws ApiException si faltan campos obligatorios, si las entidades relacionadas no existen
     *                      o no pertenecen a la misma residencia, o si se proporcionan valores inválidos.
     */
    public RegistroJuegoResponseDto add(Long idResidencia, Long idJuego,RegistroJuegoDto input) throws ApiException {
        if (input == null || input.getDuracion() == null || input.getNum() == null ||
                input.getIdResidente() == null || input.getIdUsuario() == null ||
                input.getDificultad() == null || idJuego == null || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        //Comprobar si el residente existe
        Residente residente = residenteService.findById(input.getIdResidente());
        if (residente == null)
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        //Comprobar si el residente pertence a la residencia
        if (!Objects.equals(residente.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);

        //Comprobar si el usuario existe
        User usuario = userService.findById(input.getIdUsuario());
        if (usuario == null)
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        //Comprobar si el usuario pertence a la residencia
        if (!Objects.equals(usuario.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);

        //No puede haber fallos negativos
        if (input.getNum() < 0){
            throw new ApiException(ApiErrorCode.VALORES_NEGATIVOS);
        }

        RegistroJuego registroJuego = new RegistroJuego(input);
        registroJuego.setJuego(juego);
        registroJuego.setResidente(residente);
        registroJuego.setUsuario(usuario);
        RegistroJuego registro = registroJuegoRepository.save(registroJuego);
        return new RegistroJuegoResponseDto(registro);
    }


    /**
     * Obtiene un registro de juego específico mediante su ID.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro de juego.
     * @return El registro de juego solicitado.
     * @throws ApiException si algún parámetro es nulo, si el juego no existe o no pertenece a la residencia,
     *                      o si el registro no existe o no corresponde con el juego indicado.
     */
    public RegistroJuegoResponseDto get(Long idResidencia, Long idJuego, Long idRegistroJuego) {
        if (idResidencia == null || idJuego == null || idRegistroJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        //Comprobar si existe el registro de juego
        RegistroJuego registroJuego = registroJuegoRepository.findById(idRegistroJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO));
        //Comprobar si el registro pertenece al juego
        if (!Objects.equals(registroJuego.getJuego().getId(), idJuego))
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);

        return new RegistroJuegoResponseDto(registroJuego);

    }

    /**
     * Obtiene todos los registros de juego filtrados por dificultad y parámetros opcionales.
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param dificultad Dificultad de la partida (FACIL, MEDIO, DIFICIL).
     * @param edad (opcional) Edad del residente.
     * @param minEdad (opcional) Edad mínima del residente.
     * @param maxEdad (opcional) Edad máxima del residente.
     * @param idResidente (opcional) ID del residente.
     * @param fecha (opcional) Fecha de la partida.
     * @param minFecha (opcional) Fecha mínima de la partida.
     * @param maxFecha (opcional) Fecha máxima de la partida.
     * @param promedio (opcional) Si se debe filtrar por duración promedio.
     * @param masPromedio (opcional) Si se debe filtrar por duración mayor al promedio.
     * @param menosPromedio (opcional) Si se debe filtrar por duración menor al promedio.
     * @param ordenFecha (opcional) Si se debe ordenar por fecha descendente o ascendente.
     * @return Lista de registros de juego que cumplen con los filtros.
     */
    public List<RegistroJuegoResponseDto> getAll(Long idResidencia, Long idJuego,
                                                 Dificultad dificultad, Integer edad, Integer minEdad,
                                                 Integer maxEdad, Long idResidente, LocalDate fecha,
                                                 LocalDate minFecha, LocalDate maxFecha,
                                                 boolean promedio, boolean masPromedio,
                                                 boolean menosPromedio, boolean ordenFecha) {
        if (idResidencia == null || idJuego == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        Residencia residencia = residenciaRepository.findById(idResidencia)
                .orElseThrow(() -> new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO));

        Specification<RegistroJuego> spec = RegistroJuegoSpecification.withDynamicFilters(
               idResidencia, idJuego,idResidente, edad, minEdad, maxEdad, fecha, minFecha, maxFecha, dificultad
        );

        return getRegistroJuegoResponseDtos(promedio, masPromedio, menosPromedio, ordenFecha, spec);
    }

    /**
     * Obtiene todos los registros de juego filtrados por dificultad y parámetros opcionales.
     *
     * @param idJuego ID del juego.
     * @param dificultad Dificultad de la partida (FACIL, MEDIO, DIFICIL).
     * @param edad (opcional) Edad del residente.
     * @param minEdad (opcional) Edad mínima del residente.
     * @param maxEdad (opcional) Edad máxima del residente.
     * @param idResidente (opcional) ID del residente.
     * @param fecha (opcional) Fecha de la partida.
     * @param minFecha (opcional) Fecha mínima de la partida.
     * @param maxFecha (opcional) Fecha máxima de la partida.
     * @param promedio (opcional) Si se debe filtrar por duración promedio.
     * @param masPromedio (opcional) Si se debe filtrar por duración mayor al promedio.
     * @param menosPromedio (opcional) Si se debe filtrar por duración menor al promedio.
     * @param ordenFecha (opcional) Si se debe ordenar por fecha descendente o ascendente.
     * @return Lista de registros de juego que cumplen con los filtros.
     */
    public List<RegistroJuegoResponseDto> getAll(Long idJuego,
                                                 Dificultad dificultad, Integer edad, Integer minEdad,
                                                 Integer maxEdad, Long idResidente, LocalDate fecha,
                                                 LocalDate minFecha, LocalDate maxFecha,
                                                 boolean promedio, boolean masPromedio,
                                                 boolean menosPromedio, boolean ordenFecha) {

        if (idJuego == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);


        Specification<RegistroJuego> spec = RegistroJuegoSpecification.withDynamicFilters(
                null, idJuego,idResidente, edad, minEdad, maxEdad, fecha, minFecha, maxFecha, dificultad
        );

        return getRegistroJuegoResponseDtos(promedio, masPromedio, menosPromedio, ordenFecha, spec);
    }

    private List<RegistroJuegoResponseDto> getRegistroJuegoResponseDtos(boolean promedio, boolean masPromedio, boolean menosPromedio, boolean ordenFecha, Specification<RegistroJuego> spec) {
        Sort sort = Sort.by(ordenFecha ? Sort.Direction.DESC : Sort.Direction.ASC, "fecha");
        List<RegistroJuego> registros = registroJuegoRepository.findAll(spec, sort);

        if (promedio || masPromedio || menosPromedio) {
            double duracionPromedio = registros.stream()
                    .mapToDouble(RegistroJuego::getDuracion)
                    .average()
                    .orElse(0.0);

            if (promedio) {
                // ±5% de margen respecto al promedio
                double margen = duracionPromedio * 0.05;
                registros = registros.stream()
                        .filter(r -> Math.abs(r.getDuracion() - duracionPromedio) <= margen)
                        .toList();
            } else if (masPromedio) {
                registros = registros.stream()
                        .filter(r -> r.getDuracion() > duracionPromedio)
                        .toList();
            } else if (menosPromedio) {
                registros = registros.stream()
                        .filter(r -> r.getDuracion() < duracionPromedio)
                        .toList();
            }
        }


        return registros.stream()
                .map(RegistroJuegoResponseDto::new)
                .toList();
    }


    /**
     * Obtiene todos los registros de juego filtrados por dificultad y parámetros opcionales.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param dificultad Dificultad de la partida (FACIL, MEDIO, DIFICIL).
     * @param idResidente (opcional) ID del residente.
     * @param idUser (opcional) ID del usuario que registró la partida.
     * @param year (opcional) Año en que se jugó.
     * @param month (opcional) Mes en que se jugó.
     * @return Lista de registros de juego que cumplen con los filtros.
     * @throws ApiException si faltan campos obligatorios, o si alguna de las entidades no existe
     *                      o no pertenece a la residencia indicada.
     */
    public List<RegistroJuegoResponseDto> getAll(Long idResidencia, Long idJuego, Dificultad dificultad, Long idResidente, Long idUser, Integer year, Integer month) {
        if (idResidencia == null || idJuego == null || dificultad == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        List<RegistroJuego> baseList;

        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);


        User user = null;
        if (idUser != null){
            //Comprobar si el usuario existe
            user = userService.findById(idUser);
            if (user == null)
                throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
            //Comprobar si el usuario pertence a la residencia
            if (!Objects.equals(user.getResidencia().getId(), idResidencia))
                throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }


        //Filtrar de residente y usuario
        if (idResidente == null){
            if (idUser == null){
                baseList = registroJuegoRepository.findByJuegoAndDificultad(juego, dificultad);
            }
            else
                baseList = registroJuegoRepository.findByJuegoAndDificultadAndUsuario(juego, dificultad, user);
        }
        else{
            //Comprobar si el residente existe
            Residente residente = residenteService.findById(idResidente);
            if (residente == null)
                throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
            //Comprobar si el residente pertence a la residencia
            if (!Objects.equals(residente.getResidencia().getId(), idResidencia))
                throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);

            if (idUser == null){
                baseList = registroJuegoRepository.findByJuegoAndDificultadAndResidente(juego, dificultad, residente);
            }
            else{
                baseList = registroJuegoRepository.findByJuegoAndDificultadAndResidenteAndUsuario(juego, dificultad, residente, user);
            }
        }

        //Filtra por dia, por mes, por año o combinado. //Mapping clase origen clase destino. DOZER, MapStruct
        if (year != null || month != null) {
            baseList = baseList.stream()
                    .filter(r -> {
                        boolean match = true;
                        if (year != null) match = r.getFecha().getYear() == year;
                        if (month != null) match = match && r.getFecha().getMonthValue() == month;
                        return match;
                    })
                    .toList();
        }

        return baseList.stream().map(RegistroJuegoResponseDto::new).collect(Collectors.toList());
    }

    public List<RegistroJuegoResponseDto> getAll(Long idResidencia, Long idJuego, Dificultad dificultad){
        if (idResidencia == null || idJuego == null || dificultad == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        //Comprobar si la residencia existe
        Residencia residencia = residenciaRepository.findById(idResidencia)
                .orElseThrow(() -> new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO));

        return registroJuegoRepository.findByJuegoAndDificultadAndResidente_Residencia(juego, dificultad, residencia)
                .stream().map(RegistroJuegoResponseDto::new).toList();
    }


    /**
     * Elimina un registro de juego existente.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro de juego.
     * @throws ApiException si alguno de los parámetros es nulo, si el juego o el registro no existen,
     *                      o si no pertenecen a la misma residencia o juego.
     */
    public void delete(Long idResidencia, Long idJuego, Long idRegistroJuego) {
        if (idResidencia == null || idJuego == null || idRegistroJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si existe el registro de juego
        RegistroJuego registroJuego = registroJuegoRepository.findById(idRegistroJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO));
        //Comprobar si el registro pertenece al juego
        if (!Objects.equals(registroJuego.getJuego().getId(), idJuego))
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);

        //Eliminar el registro
        registroJuegoRepository.delete(registroJuego);
    }

    /**
     * Actualiza la observación de un registro de juego existente.
     *
     * @param idResidencia ID de la residencia.
     * @param idJuego ID del juego.
     * @param idRegistroJuego ID del registro de juego.
     * @param input DTO con la nueva observación.
     * @return El registro de juego actualizado.
     * @throws ApiException si alguno de los parámetros es nulo, si el juego o el registro no existen,
     *                      o si no pertenecen a la misma residencia.
     */
    public RegistroJuegoResponseDto update(Long idResidencia, Long idJuego, Long idRegistroJuego, RegistroJuegoDto input){
        if (idResidencia == null || idJuego == null || idRegistroJuego == null)
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);

        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
     ;

        //Comprobar si existe el registro de juego
        RegistroJuego registroJuego = registroJuegoRepository.findById(idRegistroJuego).orElse(null);
        if (registroJuego == null)
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);
        //Comprobar si el registro pertenece al juego
        if (!Objects.equals(registroJuego.getJuego().getId(), idJuego))
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);

        //Comprobar si tiene observaciones el input
        if (input != null){
            if (input.getObservacion() != null){
                registroJuego.setObservacion(input.getObservacion());
                //guardar el registro
                registroJuego = registroJuegoRepository.save(registroJuego);
            }
        }
        return new RegistroJuegoResponseDto(registroJuego);
    }

    public List<RegistroJuegoResponseDto> getAll(Long idResidencia, Long idJuego) {
        if (idResidencia == null || idJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        //Comprobar si la residencia existe
        Residencia residencia = residenciaRepository.findById(idResidencia)
                .orElseThrow(() -> new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO));

        return registroJuegoRepository.findByJuegoAndResidente_Residencia(juego,residencia).stream().map(RegistroJuegoResponseDto::new).toList();

    }

    /**
     * Obtiene todos los registros de juego de un juego específico.
     *
     * @param idJuego ID del juego.
     * @return Lista de registros de juego asociados al juego.
     * @throws ApiException si el ID del juego es nulo o no existe.
     */
    public List<RegistroJuegoResponseDto> getAll(Long idJuego) {
        if (idJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        return registroJuegoRepository.findByJuego(juego).stream().map(RegistroJuegoResponseDto::new).toList();
    }

    /**
     * Obtiene todos los registros de juego de un juego específico filtrados por dificultad.
     * @param idJuego ID del juego.
     * @param dificuldad Dificultad del juego. Representa diferentes niveles de dificultad.
     * @return Lista de registros de juego asociados al juego y dificultad especificados.
     */
    public List<RegistroJuegoResponseDto> getAll(Long idJuego, Dificultad dificuldad) {
        if (idJuego == null || dificuldad == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        return registroJuegoRepository.findByJuegoAndDificultad(juego, dificuldad).stream().map(RegistroJuegoResponseDto::new).toList();

    }
}
