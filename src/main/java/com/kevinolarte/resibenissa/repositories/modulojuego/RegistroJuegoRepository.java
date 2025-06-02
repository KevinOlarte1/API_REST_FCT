package com.kevinolarte.resibenissa.repositories.modulojuego;

import com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para acceder y gestionar registros de juegos ({@link RegistroJuego}).
 * <p>
 * Permite obtener estadísticas o historiales de juegos filtrando por residencia,
 * por juego, por residente o por fecha.
 *
 * @author Kevin Olarte
 */
@Repository
public interface RegistroJuegoRepository extends JpaRepository<RegistroJuego, Long>, JpaSpecificationExecutor<RegistroJuego> {


    /**
     * Calcula el promedio diario de duración de los registros de juego de un residente específico.
     *
     * <p>
     * Se agrupan los registros por día (formato YYYY-MM-DD), y se calcula la duración media y el total de registros
     * por cada día. Permite filtrar por dificultad y por un juego concreto (ambos opcionales).
     * </p>
     *
     * @param idResidente ID del residente a analizar.
     * @param dificultad  Nivel de dificultad a filtrar (opcional, puede ser {@code null}).
     * @param idJuego     ID del juego a filtrar (opcional, puede ser {@code null} para incluir todos).
     * @return Lista de {@link MediaRegistroDTO} con fecha (día), duración media y cantidad de registros por día.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    """)
    List<MediaRegistroDTO> getMediaDuracionDiaria(@Param("idResidente") Long idResidente,
                                                  @Param("dificultad") Dificultad dificultad,
                                                  @Param("idJuego") Long idJuego);


    /**
     * Calcula el promedio mensual de duración de los registros de juego de un residente específico.
     *
     * <p>
     * Agrupa por mes (formato YYYY-MM), mostrando duración media y número de registros por cada mes.
     * Se puede filtrar por dificultad y juego (ambos opcionales).
     * </p>
     *
     * @param idResidente ID del residente.
     * @param dificultad  Dificultad del juego (opcional).
     * @param idJuego     ID del juego a filtrar (opcional, puede ser {@code null} para todos los juegos).
     * @return Lista de {@link MediaRegistroDTO} con la media mensual de duración y el total de registros.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
    """)
    List<MediaRegistroDTO> getMediaDuracionMensual(@Param("idResidente") Long idResidente,
                                                   @Param("dificultad") Dificultad dificultad,
                                                   @Param("idJuego") Long idJuego);


    /**
     * Calcula el promedio anual de duración de los registros de juego de un residente específico.
     *
     * <p>
     * Agrupa por año (YYYY) y devuelve la duración media y el total de registros de cada año.
     * Se puede aplicar filtro por dificultad y juego.
     * </p>
     *
     * @param idResidente ID del residente cuyos registros se agrupan.
     * @param dificultad  Dificultad del juego (opcional).
     * @param idJuego     ID del juego a filtrar (opcional, puede ser {@code null}).
     * @return Lista de {@link MediaRegistroDTO} con año, duración media y conteo de registros por año.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
    """)
    List<MediaRegistroDTO> getMediaDuracionAnual(@Param("idResidente") Long idResidente,
                                                 @Param("dificultad") Dificultad dificultad,
                                                 @Param("idJuego") Long idJuego);



    /**
     * Calcula el promedio diario de errores cometidos por un residente específico.
     *
     * <p>
     * Agrupa los registros por día (formato YYYY-MM-DD), y calcula el promedio de errores (`num`)
     * y el total de registros por día. Permite filtrar por dificultad y juego de forma opcional.
     * </p>
     *
     * @param idResidente ID del residente a analizar.
     * @param dificultad  Nivel de dificultad del juego (opcional).
     * @param idJuego     ID del juego a filtrar (opcional, {@code null} para incluir todos).
     * @return Lista de {@link MediaRegistroDTO} con fecha (día), promedio de errores y número total de registros por día.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    """)
    List<MediaRegistroDTO> getMediaErroresDiario(@Param("idResidente") Long idResidente,
                                                 @Param("dificultad") Dificultad dificultad,
                                                 @Param("idJuego") Long idJuego);


    /**
     * Calcula el promedio mensual de errores cometidos por un residente específico.
     *
     * <p>
     * Agrupa por mes (formato YYYY-MM), y devuelve el promedio de errores (`num`)
     * junto con el total de registros por cada mes. Se puede filtrar por dificultad y juego.
     * </p>
     *
     * @param idResidente ID del residente.
     * @param dificultad  Dificultad del juego (opcional).
     * @param idJuego     ID del juego a filtrar (opcional, {@code null} para incluir todos).
     * @return Lista de {@link MediaRegistroDTO} con promedio mensual de errores y conteo de registros.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
    """)
    List<MediaRegistroDTO> getMediaErroresMensual(@Param("idResidente") Long idResidente,
                                                  @Param("dificultad") Dificultad dificultad,
                                                  @Param("idJuego") Long idJuego);


    /**
     * Calcula el promedio anual de errores cometidos por un residente específico.
     *
     * <p>
     * Agrupa por año (YYYY), devolviendo el promedio de errores (`num`)
     * y el total de registros por cada año. Admite filtros opcionales por dificultad y juego.
     * </p>
     *
     * @param idResidente ID del residente cuyos registros se analizarán.
     * @param dificultad  Nivel de dificultad (opcional).
     * @param idJuego     ID del juego (opcional, {@code null} para todos los juegos).
     * @return Lista de {@link MediaRegistroDTO} con año, promedio de errores y total de registros por año.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
    """)
    List<MediaRegistroDTO> getMediaErroresAnual(@Param("idResidente") Long idResidente,
                                                @Param("dificultad") Dificultad dificultad,
                                                @Param("idJuego") Long idJuego);



    /**
     * Calcula el promedio mensual de duración de los juegos jugados por todos los residentes
     * de una residencia, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param dificultad   Dificultad específica a filtrar (puede ser null para ignorar el filtro).
     * @param idJuego      ID del juego a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un mes y su promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
    """)
    List<MediaRegistroDTO> getMediaDuracionResidenciaMensual(@Param("idResidencia") Long idResidencia,
                                                             @Param("dificultad") Dificultad dificultad,
                                                             @Param("idJuego") Long idJuego);

    /**
     * Calcula el promedio anual de duración de los juegos jugados por todos los residentes
     * de una residencia, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param dificultad   Dificultad específica a filtrar (puede ser null para ignorar el filtro).
     * @param idJuego      ID del juego a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un año y su promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
    """)
    List<MediaRegistroDTO> getMediaDuracionResidenciaAnual(@Param("idResidencia") Long idResidencia,
                                                           @Param("dificultad") Dificultad dificultad,
                                                           @Param("idJuego") Long idJuego);

    /**
     * Calcula el promedio diario de errores cometidos en los juegos por todos los residentes
     * de una residencia específica, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes se van a considerar.
     * @param dificultad   Dificultad de los juegos a filtrar (si es null, se consideran todas las dificultades).
     * @param idJuego      ID del juego a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un día y el promedio de errores cometidos.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    """)
    List<MediaRegistroDTO> getMediaErroresResidenciaDiaria(@Param("idResidencia") Long idResidencia,
                                                           @Param("dificultad") Dificultad dificultad,
                                                           @Param("idJuego") Long idJuego);

    /**
     * Calcula el promedio diario de duración de los juegos jugados por todos los residentes
     * de una residencia, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param idJuego      ID del juego a filtrar (puede ser null para ignorar el filtro).
     * @param dificultad   Dificultad específica a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un día y su promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    """)
    List<MediaRegistroDTO> getMediaDuracionResidenciaDiaria(@Param("idResidencia") Long idResidencia,
                                                            @Param("dificultad") Dificultad dificultad,
                                                            @Param("idJuego") Long idJuego);

    /**
     * Calcula el promedio mensual de errores cometidos en los juegos por todos los residentes
     * de una residencia específica, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes se van a considerar.
     * @param dificultad   Dificultad de los juegos a filtrar (si es null, se consideran todas las dificultades).
     * @param idJuego      ID del juego a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un mes y el promedio de errores cometidos.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
    """)
    List<MediaRegistroDTO> getMediaErroresResidenciaMensual(@Param("idResidencia") Long idResidencia,
                                                            @Param("dificultad") Dificultad dificultad,
                                                            @Param("idJuego") Long idJuego);

    /**
     * Calcula el promedio anual de errores cometidos en los juegos por todos los residentes
     * de una residencia específica, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes se van a considerar.
     * @param dificultad   Dificultad de los juegos a filtrar (si es null, se consideran todas las dificultades).
     * @param idJuego      ID del juego a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un año y el promedio de errores cometidos.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
    """)
    List<MediaRegistroDTO> getMediaErroresResidenciaAnual(@Param("idResidencia") Long idResidencia,
                                                          @Param("dificultad") Dificultad dificultad,
                                                          @Param("idJuego") Long idJuego);


        /**
         * Calcula el promedio diario de errores a nivel global,
         * con posibilidad de filtrar por dificultad y juego.
         */
        @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(
        TO_CHAR(r.fecha, 'YYYY-MM-DD'),
        AVG(r.num),
        COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    """)
    List<MediaRegistroDTO> getMediaErroresGlobalDiaria(@Param("dificultad") Dificultad dificultad,
                                                       @Param("idJuego") Long idJuego);


    /**
     * Calcula el promedio mensual de errores a nivel global,
     * con posibilidad de filtrar por dificultad y juego.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(
        TO_CHAR(r.fecha, 'YYYY-MM'),
        AVG(r.num),
        COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
    """)
    List<MediaRegistroDTO> getMediaErroresGlobalMensual(@Param("dificultad") Dificultad dificultad,
                                                        @Param("idJuego") Long idJuego);

    /**
     * Calcula el promedio anual de errores a nivel global,
     * con posibilidad de filtrar por dificultad y juego.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(
        CAST(EXTRACT(YEAR FROM r.fecha) AS string),
        AVG(r.num),
        COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
    """)
    List<MediaRegistroDTO> getMediaErroresGlobalAnual(@Param("dificultad") Dificultad dificultad,
                                                      @Param("idJuego") Long idJuego);


    /**
     * Calcula la media diaria de duración de juegos a nivel global,
     * con posibilidad de filtrar por dificultad y juego.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(
        TO_CHAR(r.fecha, 'YYYY-MM-DD'),
        AVG(r.duracion),
        COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    """)
    List<MediaRegistroDTO> getMediaDuracionGlobalDiaria(@Param("dificultad") Dificultad dificultad,
                                                        @Param("idJuego") Long idJuego);

    /**
     * Calcula la media mensual de duración de juegos a nivel global,
     * con posibilidad de filtrar por dificultad y juego.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(
        TO_CHAR(r.fecha, 'YYYY-MM'),
        AVG(r.duracion),
        COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
    """)
    List<MediaRegistroDTO> getMediaDuracionGlobalMensual(@Param("dificultad") Dificultad dificultad,
                                                         @Param("idJuego") Long idJuego);


    /**
     * Calcula la media anual de duración de juegos a nivel global,
     * con posibilidad de filtrar por dificultad y juego.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(
        CAST(EXTRACT(YEAR FROM r.fecha) AS string),
        AVG(r.duracion),
        COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    AND (:idJuego IS NULL OR r.juego.id = :idJuego)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
    """)
    List<MediaRegistroDTO> getMediaDuracionGlobalAnual(@Param("dificultad") Dificultad dificultad,
                                                       @Param("idJuego") Long idJuego);



    /**
     * Calcula el promedio diario de errores en los juegos jugados por todos los residentes para un juego específico.
     *
     * @param idJuego     ID del juego a analizar.
     * @param dificultad  Dificultad a filtrar (puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} agrupada por día.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaErroresPorJuegoDiaria(@Param("idJuego") Long idJuego,
                                                        @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio mensual de errores para un juego específico.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaErroresPorJuegoMensual(@Param("idJuego") Long idJuego,
                                                         @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de errores para un juego específico.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaErroresPorJuegoAnual(@Param("idJuego") Long idJuego,
                                                       @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio diario de duración de un juego específico.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.duracion),COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaDuracionPorJuegoDiaria(@Param("idJuego") Long idJuego,
                                                          @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio mensual de duración de un juego específico.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaDuracionPorJuegoMensual(@Param("idJuego") Long idJuego,
                                                           @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de duración de un juego específico.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaDuracionPorJuegoAnual(@Param("idJuego") Long idJuego,
                                                         @Param("dificultad") Dificultad dificultad);


    /**
     * Calcula el promedio diario de duración del juego especificado,
     * considerando únicamente los registros pertenecientes a los residentes de una residencia específica.
     *
     * @param idJuego      ID del juego cuyos registros se analizarán.
     * @param idResidencia ID de la residencia cuyos residentes serán tenidos en cuenta.
     * @param dificultad   Nivel de dificultad a filtrar (opcional, puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} donde cada entrada representa un día con el promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.duracion),COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaDuracionPorJuegoYResidenciaDiaria(@Param("idJuego") Long idJuego,
                                                                     @Param("idResidencia") Long idResidencia,
                                                                     @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio mensual de duración del juego especificado,
     * considerando únicamente los registros pertenecientes a los residentes de una residencia específica.
     *
     * @param idJuego      ID del juego cuyos registros se analizarán.
     * @param idResidencia ID de la residencia cuyos residentes serán tenidos en cuenta.
     * @param dificultad   Nivel de dificultad a filtrar (opcional, puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} donde cada entrada representa un mes con el promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaDuracionPorJuegoYResidenciaMensual(@Param("idJuego") Long idJuego,
                                                                      @Param("idResidencia") Long idResidencia,
                                                                      @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de duración del juego especificado,
     * considerando únicamente los registros pertenecientes a los residentes de una residencia específica.
     *
     * @param idJuego      ID del juego cuyos registros se analizarán.
     * @param idResidencia ID de la residencia cuyos residentes serán tenidos en cuenta.
     * @param dificultad   Nivel de dificultad a filtrar (opcional, puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} donde cada entrada representa un año con el promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaDuracionPorJuegoYResidenciaAnual(@Param("idJuego") Long idJuego,
                                                                    @Param("idResidencia") Long idResidencia,
                                                                    @Param("dificultad") Dificultad dificultad);


    /**
     * Calcula el promedio diario de errores del juego especificado,
     * considerando únicamente los registros pertenecientes a los residentes de una residencia específica.
     *
     * @param idJuego      ID del juego cuyos registros se analizarán.
     * @param idResidencia ID de la residencia cuyos residentes serán tenidos en cuenta.
     * @param dificultad   Nivel de dificultad a filtrar (opcional, puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} donde cada entrada representa un día con el promedio de errores.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaErroresPorJuegoYResidenciaDiaria(@Param("idJuego") Long idJuego,
                                                                   @Param("idResidencia") Long idResidencia,
                                                                   @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio mensual de errores del juego especificado,
     * considerando únicamente los registros pertenecientes a los residentes de una residencia específica.
     *
     * @param idJuego      ID del juego cuyos registros se analizarán.
     * @param idResidencia ID de la residencia cuyos residentes serán tenidos en cuenta.
     * @param dificultad   Nivel de dificultad a filtrar (opcional, puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} donde cada entrada representa un mes con el promedio de errores.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaErroresPorJuegoYResidenciaMensual(@Param("idJuego") Long idJuego,
                                                                    @Param("idResidencia") Long idResidencia,
                                                                    @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de errores del juego especificado,
     * considerando únicamente los registros pertenecientes a los residentes de una residencia específica.
     *
     * @param idJuego      ID del juego cuyos registros se analizarán.
     * @param idResidencia ID de la residencia cuyos residentes serán tenidos en cuenta.
     * @param dificultad   Nivel de dificultad a filtrar (opcional, puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} donde cada entrada representa un año con el promedio de errores.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.juego.id = :idJuego AND r.residente.residencia.id = :idResidencia
    AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaErroresPorJuegoYResidenciaAnual(@Param("idJuego") Long idJuego,
                                                                  @Param("idResidencia") Long idResidencia,
                                                                  @Param("dificultad") Dificultad dificultad);

}
