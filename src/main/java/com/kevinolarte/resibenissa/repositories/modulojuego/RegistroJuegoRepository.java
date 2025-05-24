package com.kevinolarte.resibenissa.repositories.modulojuego;

import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
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



}
