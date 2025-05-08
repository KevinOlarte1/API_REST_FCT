package com.kevinolarte.resibenissa.repositories.modulojuego;

import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    List<RegistroJuego> findByJuego_Id(Long juegoId);

    List<RegistroJuego> findByJuego_IdAndUsuario_Id(Long juegoId, Long usuarioId);

    List<RegistroJuego> findByJuego_IdAndResidente_Id(Long juegoId, Long residenteId);

    List<RegistroJuego> findByJuego_IdAndResidente_IdAndUsuario_Id(Long juegoId, Long residenteId, Long usuarioId);

    List<RegistroJuego> findByJuegoAndUsuario(Juego juego, User usuario);

    List<RegistroJuego> findByJuegoAndResidente(Juego juego, Residente residente);

    List<RegistroJuego> findByJuegoAndResidenteAndUsuario(Juego juego, Residente residente, User usuario);

    List<RegistroJuego> findByJuego(Juego juego);

    List<RegistroJuego> findByJuegoAndDificultad(Juego juego, Dificultad dificultad);

    List<RegistroJuego> findByJuegoAndDificultadAndUsuario(Juego juego, Dificultad dificultad, User usuario);

    List<RegistroJuego> findByJuegoAndDificultadAndResidente(Juego juego, Dificultad dificultad, Residente residente);

    List<RegistroJuego> findByJuegoAndDificultadAndResidenteAndUsuario(Juego juego, Dificultad dificultad, Residente residente, User usuario);
}
