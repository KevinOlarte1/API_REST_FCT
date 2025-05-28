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
     * Obtiene un registro de juego específico mediante su juego
     * @param juego Juego del que se desea obtener el registro.
     * @return Lista de registros de juego asociados al juego especificado.
     */
    List<RegistroJuego> findByJuego(Juego juego);

    /**
     * Lista de registros de un juego específico y una dificultad concreta.
     * @param juego Juego del que se desea obtener el registro.
     * @param dificultad Dificultad del juego que se desea filtrar.
     * @return Lista de registros de juego asociados al juego y dificultad especificados.
     */
    List<RegistroJuego> findByJuegoAndDificultad(Juego juego, Dificultad dificultad);


    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaDuracionDiaria(@Param("idResidente") Long idResidente,
                                                  @Param("dificultad") Dificultad dificultad);

    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaDuracionMensual(@Param("idResidente") Long idResidente,
                                                   @Param("dificultad") Dificultad dificultad);

    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaDuracionAnual(@Param("idResidente") Long idResidente,
                                                 @Param("dificultad") Dificultad dificultad);


    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaErroresDiario(@Param("idResidente") Long idResidente,
                                                @Param("dificultad") Dificultad dificultad);

    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaErroresMensual(@Param("idResidente") Long idResidente,
                                                 @Param("dificultad") Dificultad dificultad);

    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.id = :idResidente AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaErroresAnual(@Param("idResidente") Long idResidente,
                                               @Param("dificultad") Dificultad dificultad);


    /**
     * Calcula el promedio diario de duración de los juegos jugados por todos los residentes
     * de una residencia, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param dificultad   Dificultad específica a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un día y su promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaDuracionResidenciaDiaria(@Param("idResidencia") Long idResidencia,
                                                            @Param("dificultad") Dificultad dificultad);


    /**
     * Calcula el promedio mensual de duración de los juegos jugados por todos los residentes
     * de una residencia, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param dificultad   Dificultad específica a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un mes y su promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaDuracionResidenciaMensual(@Param("idResidencia") Long idResidencia,
                                                             @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de duración de los juegos jugados por todos los residentes
     * de una residencia, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes serán considerados.
     * @param dificultad   Dificultad específica a filtrar (puede ser null para ignorar el filtro).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un año y su promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaDuracionResidenciaAnual(@Param("idResidencia") Long idResidencia,
                                                           @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio diario de errores cometidos en los juegos por todos los residentes
     * de una residencia específica, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes se van a considerar.
     * @param dificultad   Dificultad de los juegos a filtrar (si es null, se consideran todas las dificultades).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un día y el promedio de errores cometidos.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaErroresResidenciaDiaria(@Param("idResidencia") Long idResidencia,
                                                          @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio mensual de errores cometidos en los juegos por todos los residentes
     * de una residencia específica, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes se van a considerar.
     * @param dificultad   Dificultad de los juegos a filtrar (si es null, se consideran todas las dificultades).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un mes y el promedio de errores cometidos.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaErroresResidenciaMensual(@Param("idResidencia") Long idResidencia,
                                                           @Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de errores cometidos en los juegos por todos los residentes
     * de una residencia específica, con opción de filtrar por dificultad.
     *
     * @param idResidencia ID de la residencia cuyos residentes se van a considerar.
     * @param dificultad   Dificultad de los juegos a filtrar (si es null, se consideran todas las dificultades).
     * @return Lista de {@link MediaRegistroDTO} donde cada elemento representa un año y el promedio de errores cometidos.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE r.residente.residencia.id = :idResidencia AND (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaErroresResidenciaAnual(@Param("idResidencia") Long idResidencia,
                                                         @Param("dificultad") Dificultad dificultad);


    /**
     * Calcula el promedio diario de errores cometidos en los juegos por todos los residentes
     * del sistema, con opción de filtrar por dificultad.
     *
     * @param dificultad Dificultad del juego a filtrar (puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} agrupada por día con el promedio de errores.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaErroresGlobalDiaria(@Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio mensual de errores cometidos en los juegos por todos los residentes
     * del sistema, con opción de filtrar por dificultad.
     *
     * @param dificultad Dificultad del juego a filtrar (puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} agrupada por mes con el promedio de errores.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaErroresGlobalMensual(@Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de errores cometidos en los juegos por todos los residentes
     * del sistema, con opción de filtrar por dificultad.
     *
     * @param dificultad Dificultad del juego a filtrar (puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} agrupada por año con el promedio de errores.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.num), COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaErroresGlobalAnual(@Param("dificultad") Dificultad dificultad);


    /**
     * Calcula el promedio diario de duración de los juegos jugados por todos los residentes
     * del sistema, con opción de filtrar por dificultad.
     *
     * @param dificultad Dificultad del juego a filtrar (puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} agrupada por día con el promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM-DD'), AVG(r.duracion),COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM-DD')
""")
    List<MediaRegistroDTO> getMediaDuracionGlobalDiaria(@Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio mensual de duración de los juegos jugados por todos los residentes
     * del sistema, con opción de filtrar por dificultad.
     *
     * @param dificultad Dificultad del juego a filtrar (puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} agrupada por mes con el promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(TO_CHAR(r.fecha, 'YYYY-MM'), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY TO_CHAR(r.fecha, 'YYYY-MM')
    ORDER BY TO_CHAR(r.fecha, 'YYYY-MM')
""")
    List<MediaRegistroDTO> getMediaDuracionGlobalMensual(@Param("dificultad") Dificultad dificultad);

    /**
     * Calcula el promedio anual de duración de los juegos jugados por todos los residentes
     * del sistema, con opción de filtrar por dificultad.
     *
     * @param dificultad Dificultad del juego a filtrar (puede ser null para incluir todas).
     * @return Lista de {@link MediaRegistroDTO} agrupada por año con el promedio de duración.
     */
    @Query("""
    SELECT new com.kevinolarte.resibenissa.dto.out.modulojuego.MediaRegistroDTO(CAST(EXTRACT(YEAR FROM r.fecha) AS string), AVG(r.duracion), COUNT(r))
    FROM RegistroJuego r
    WHERE (:dificultad IS NULL OR r.dificultad = :dificultad)
    GROUP BY EXTRACT(YEAR FROM r.fecha)
    ORDER BY EXTRACT(YEAR FROM r.fecha)
""")
    List<MediaRegistroDTO> getMediaDuracionGlobalAnual(@Param("dificultad") Dificultad dificultad);


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
