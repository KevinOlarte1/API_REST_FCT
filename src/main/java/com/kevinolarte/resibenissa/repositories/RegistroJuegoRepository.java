package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.RegistroJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
     * Obtiene todos los registros de juegos realizados por residentes de una residencia específica.
     *
     * @param idResidencia ID de la residencia.
     * @return Lista de registros de juegos.
     */
    List<RegistroJuego> findByResidente_Residencia_Id(Long idResidencia);

    /**
     * Obtiene todos los registros de un residente específico.
     *
     * @param idResidencia (Nota: Este parámetro parece mal nombrado, debería ser `idResidente`)
     * @return Lista de registros del residente.
     */
    List<RegistroJuego> findByResidenteId(Long idResidencia);

}
