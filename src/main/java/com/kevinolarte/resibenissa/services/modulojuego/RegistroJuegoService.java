package com.kevinolarte.resibenissa.services.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO;
import com.kevinolarte.resibenissa.dto.out.modulojuego.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.enums.Filtrado.RegistroJuegoFiltrado;
import com.kevinolarte.resibenissa.enums.modulojuego.TipoAgrupacion;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
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
     * @throws ResiException si faltan campos obligatorios, si las entidades relacionadas no existen
     *                      o no pertenecen a la misma residencia, o si se proporcionan valores inválidos.
     */
    public RegistroJuegoResponseDto add(Long idResidencia, Long idJuego,RegistroJuegoDto input) throws ResiException {
        if (input == null || input.getDuracion() == null || input.getNum() == null ||
                input.getIdResidente() == null || input.getIdUsuario() == null ||
                input.getDificultad() == null || idJuego == null || idResidencia == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //Comprobar si el juego existe
        Juego juego = juegoService.getJuego(idJuego);
        if (juego == null)
            throw new ResiException(ApiErrorCode.JUEGO_INVALIDO);

        //Comprobar si el residente existe
        Residente residente = residenteService.getResidente(idResidencia, input.getIdResidente());

        //Comprobar si el usuario existe
        User usuario = userService.getUsuario(idResidencia, input.getIdUsuario());

        //No puede haber fallos negativos
        if (input.getNum() < 0){
            throw new ResiException(ApiErrorCode.VALORES_NEGATIVOS);
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
     * @param idRegistroJuego ID del registro de juego.
     * @return El registro de juego solicitado.
     * @throws ResiException si algún parámetro es nulo, si el juego no existe o no pertenece a la residencia,
     *                      o si el registro no existe o no corresponde con el juego indicado.
     */
    public RegistroJuegoResponseDto get(Long idResidencia, Long idRegistroJuego) {
        RegistroJuego registroJuego = getRegistro(idResidencia, idRegistroJuego);

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
     * @param filtrado (opcional) Si se debe ordenar por fecha descendente o ascendente.
     * @return Lista de registros de juego que cumplen con los filtros.
     */
    public List<RegistroJuegoResponseDto> getAll(Long idResidencia, Long idJuego,
                                                 Dificultad dificultad, Integer edad, Integer minEdad,
                                                 Integer maxEdad, Long idResidente, LocalDate fecha,
                                                 LocalDate minFecha, LocalDate maxFecha,
                                                 boolean promedio, boolean masPromedio,
                                                 boolean menosPromedio, RegistroJuegoFiltrado filtrado,
                                                 Boolean comentado) {
        if (idResidencia == null) {
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        Residencia residencia = residenciaRepository.findById(idResidencia)
                .orElseThrow(() -> new ResiException(ApiErrorCode.RESIDENCIA_INVALIDO));

        Specification<RegistroJuego> spec = RegistroJuegoSpecification.withDynamicFilters(
               idResidencia, idJuego,idResidente, edad, minEdad, maxEdad, fecha, minFecha, maxFecha, dificultad, comentado,
                promedio, masPromedio, menosPromedio
        );


        Sort sort = (filtrado != null) ? filtrado.toSort() : Sort.by(Sort.Direction.DESC, "fecha");
        List<RegistroJuego> registros = registroJuegoRepository.findAll(spec, sort);


        return registros.stream()
                .map(RegistroJuegoResponseDto::new)
                .collect(Collectors.toList());


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
     * @param filtrado (opcional) Si se debe ordenar por fecha descendente o ascendente.
     * @return Lista de registros de juego que cumplen con los filtros.
     */
    public List<RegistroJuegoResponseDto> getAll(Long idJuego,
                                                 Dificultad dificultad, Integer edad, Integer minEdad,
                                                 Integer maxEdad, Long idResidente, LocalDate fecha,
                                                 LocalDate minFecha, LocalDate maxFecha,
                                                 boolean promedio, boolean masPromedio,
                                                 boolean menosPromedio, RegistroJuegoFiltrado filtrado,
                                                 Boolean comentado) {

        Specification<RegistroJuego> spec = RegistroJuegoSpecification.withDynamicFilters(
                null, idJuego,idResidente, edad, minEdad, maxEdad, fecha, minFecha, maxFecha, dificultad, comentado,
                promedio, masPromedio, menosPromedio

        );



        Sort sort = (filtrado != null) ? filtrado.toSort() : Sort.by(Sort.Direction.DESC, "fecha");
        List<RegistroJuego> registros = registroJuegoRepository.findAll(spec, sort);

        return registros.stream()
                .map(RegistroJuegoResponseDto::new)
                .collect(Collectors.toList());
    }




    /**
     * Elimina un registro de juego existente.
     *
     * @param idResidencia ID de la residencia.
     * @param idRegistroJuego ID del registro de juego.
     * @throws ResiException si alguno de los parámetros es nulo, si el juego o el registro no existen,
     *                      o si no pertenecen a la misma residencia o juego.
     */
    public void delete(Long idResidencia, Long idRegistroJuego) {
        RegistroJuego registroJuego = getRegistro(idResidencia, idRegistroJuego);

        //Eliminar el registro
        registroJuegoRepository.delete(registroJuego);
    }




    /**
     * Actualiza la observación de un registro de juego existente.
     *
     * @param idResidencia ID de la residencia.
     * @param idRegistroJuego ID del registro de juego.
     * @param input DTO con la nueva observación.
     * @return El registro de juego actualizado.
     * @throws ResiException si alguno de los parámetros es nulo, si el juego o el registro no existen,
     *                      o si no pertenecen a la misma residencia.
     */
    public RegistroJuegoResponseDto update(Long idResidencia, Long idRegistroJuego, RegistroJuegoDto input){
        if (idResidencia == null || idRegistroJuego == null)
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);

        RegistroJuego registroJuego = getRegistro(idResidencia, idRegistroJuego);

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








    /**
     * Obtiene un registro de juego específico y valida que pertenezca a la residencia indicada.
     *
     * @param idResidencia ID de la residencia.
     * @param idRegistroJuego ID del registro de juego.
     * @return El registro de juego solicitado.
     * @throws ResiException si algún parámetro es nulo, si el registro no existe o no pertenece a la residencia.
     */
    RegistroJuego getRegistro(Long idResidencia, Long idRegistroJuego) {
        if (idResidencia == null || idRegistroJuego == null){
            throw new ResiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //Comprobar si existe el registro de juego
        RegistroJuego registroJuego = registroJuegoRepository.findById(idRegistroJuego)
                .orElseThrow(() -> new ResiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO));
        //Comprobar si el registro pertenece al juego a la residencia.
        if (!Objects.equals(registroJuego.getResidente().getResidencia().getId(), idResidencia))
            throw new ResiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);
        return registroJuego;
    }

    /**
     * Calcula el promedio de duración de los registros de juego de un residente,
     * agrupando los datos por día, mes o año según el tipo especificado.
     *
     * @param idResidencia ID de la residencia a la que pertenece el residente. Se utiliza para validar la pertenencia.
     * @param idResidente  ID del residente cuyos registros de juego se van a analizar.
     * @param tipo         Tipo de agrupación temporal: DIARIO, MENSUAL o ANUAL.
     * @return Lista de objetos {@link MediaRegistroDTO}, donde cada elemento representa un grupo temporal (día, mes o año)
     *         y el promedio de duración de los juegos jugados en ese grupo.
     * @throws ResiException si el residente no pertenece a la residencia indicada.
     */
    public List<MediaRegistroDTO> getMediaDuracion(Long idResidencia, Long idResidente, TipoAgrupacion tipo, Dificultad dificultad) {
        Residente residente = residenteService.getResidente(idResidencia, idResidente);

        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaDuracionDiaria(idResidente, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaDuracionMensual(idResidente, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaDuracionAnual(idResidente, dificultad);
        };
    }

    /**
     * Calcula el promedio de errores cometidos por un residente agrupado por día, mes o año.
     *
     * @param idResidencia ID de la residencia del residente.
     * @param idResidente  ID del residente.
     * @param tipo         Tipo de agrupación temporal: DIARIO, MENSUAL o ANUAL.
     * @return Lista de {@link MediaRegistroDTO} con agrupación y promedio de errores.
     * @throws ResiException si el residente no pertenece a la residencia.
     */
    public List<MediaRegistroDTO> getMediaErrores(Long idResidencia, Long idResidente, TipoAgrupacion tipo, Dificultad dificultad) {
        Residente residente = residenteService.getResidente(idResidencia, idResidente);

        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaErroresDiario(idResidente, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaErroresMensual(idResidente, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaErroresAnual(idResidente, dificultad);
        };
    }


    public List<MediaRegistroDTO> getMediaDuracionPorResidencia(Long idResidencia, TipoAgrupacion tipo, Dificultad dificultad) {
        Residencia residencia = residenciaService.getResidencia(idResidencia);
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaDuracionResidenciaDiaria(idResidencia, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaDuracionResidenciaMensual(idResidencia, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaDuracionResidenciaAnual(idResidencia, dificultad);
        };
    }

    public List<MediaRegistroDTO> getMediaErroresPorResidencia(Long idResidencia, TipoAgrupacion tipo, Dificultad dificultad) {
        Residencia residencia = residenciaService.getResidencia(idResidencia);
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaErroresResidenciaDiaria(idResidencia, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaErroresResidenciaMensual(idResidencia, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaErroresResidenciaAnual(idResidencia, dificultad);
        };
    }

    public List<MediaRegistroDTO> getMediaDuracionGlobal(TipoAgrupacion tipo, Dificultad dificultad) {
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaDuracionGlobalDiaria(dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaDuracionGlobalMensual(dificultad);
            case ANUAL -> registroJuegoRepository.getMediaDuracionGlobalAnual(dificultad);
        };
    }

    public List<MediaRegistroDTO> getMediaErroresGlobal(TipoAgrupacion tipo, Dificultad dificultad) {
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaErroresGlobalDiaria(dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaErroresGlobalMensual(dificultad);
            case ANUAL -> registroJuegoRepository.getMediaErroresGlobalAnual(dificultad);
        };
    }

    /**
     * Calcula el promedio de errores agrupado por fecha (día, mes o año) para un juego específico,
     * con opción de filtrar por dificultad.
     *
     * @param idJuego    ID del juego.
     * @param tipo       Tipo de agrupación: DIARIO, MENSUAL o ANUAL.
     * @param dificultad Dificultad a filtrar (puede ser null).
     * @return Lista de {@link MediaRegistroDTO} con la media de errores agrupada por el tipo indicado.
     */
    public List<MediaRegistroDTO> getMediaErroresPorJuego(Long idJuego, TipoAgrupacion tipo, Dificultad dificultad) {
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaErroresPorJuegoDiaria(idJuego, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaErroresPorJuegoMensual(idJuego, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaErroresPorJuegoAnual(idJuego, dificultad);
        };
    }

    /**
     * Calcula el promedio de duración agrupado por fecha (día, mes o año) para un juego específico,
     * con opción de filtrar por dificultad.
     *
     * @param idJuego    ID del juego.
     * @param tipo       Tipo de agrupación: DIARIO, MENSUAL o ANUAL.
     * @param dificultad Dificultad a filtrar (puede ser null).
     * @return Lista de {@link MediaRegistroDTO} con la media de duración agrupada por el tipo indicado.
     */
    public List<MediaRegistroDTO> getMediaDuracionPorJuego(Long idJuego, TipoAgrupacion tipo, Dificultad dificultad) {
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaDuracionPorJuegoDiaria(idJuego, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaDuracionPorJuegoMensual(idJuego, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaDuracionPorJuegoAnual(idJuego, dificultad);
        };
    }

    /**
     * Obtiene el promedio de duración de los juegos jugados por los residentes de una residencia específica
     * en un juego concreto, agrupado según el tipo de agrupación temporal proporcionado (DIARIO, MENSUAL o ANUAL).
     *
     * @param idJuego      ID del juego a analizar.
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param tipo         Tipo de agrupación temporal (DIARIO, MENSUAL o ANUAL).
     * @param dificultad   Nivel de dificultad para filtrar (opcional, puede ser null para incluir todos).
     * @return Lista de {@link MediaRegistroDTO} representando la duración promedio agrupada por el tipo indicado.
     */
    public List<MediaRegistroDTO> getMediaDuracionPorJuegoYResidencia(Long idJuego, Long idResidencia, TipoAgrupacion tipo, Dificultad dificultad) {
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaDuracionPorJuegoYResidenciaDiaria(idJuego, idResidencia, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaDuracionPorJuegoYResidenciaMensual(idJuego, idResidencia, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaDuracionPorJuegoYResidenciaAnual(idJuego, idResidencia, dificultad);
        };
    }

    /**
     * Obtiene el promedio de errores cometidos en los juegos jugados por los residentes de una residencia específica
     * en un juego concreto, agrupado según el tipo de agrupación temporal proporcionado (DIARIO, MENSUAL o ANUAL).
     *
     * @param idJuego      ID del juego a analizar.
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param tipo         Tipo de agrupación temporal (DIARIO, MENSUAL o ANUAL).
     * @param dificultad   Nivel de dificultad para filtrar (opcional, puede ser null para incluir todos).
     * @return Lista de {@link MediaRegistroDTO} representando la media de errores agrupada por el tipo indicado.
     */
    public List<MediaRegistroDTO> getMediaErroresPorJuegoYResidencia(Long idJuego, Long idResidencia, TipoAgrupacion tipo, Dificultad dificultad) {
        return switch (tipo) {
            case DIARIO -> registroJuegoRepository.getMediaErroresPorJuegoYResidenciaDiaria(idJuego, idResidencia, dificultad);
            case MENSUAL -> registroJuegoRepository.getMediaErroresPorJuegoYResidenciaMensual(idJuego, idResidencia, dificultad);
            case ANUAL -> registroJuegoRepository.getMediaErroresPorJuegoYResidenciaAnual(idJuego, idResidencia, dificultad);
        };
    }

}
