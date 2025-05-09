package com.kevinolarte.resibenissa.repositories.modulojuego;

import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para acceder y gestionar entidades de tipo {@link Juego}.
 * <p>
 * Extiende {@link JpaRepository} para aprovechar métodos CRUD y de paginación,
 * y define métodos personalizados para búsquedas por nombre y residencia.
 *
 * @author Kevin Olarte
 */
@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    /**
     * Verifica si ya existe un juego con el mismo nombre en una residencia específica.
     *
     * @param nombre       Nombre del juego.
     * @param residenciaId ID de la residencia.
     * @return true si existe un juego con ese nombre en la residencia, false en caso contrario.
     */
    boolean existsByNombreAndResidenciaId(String nombre, Long residenciaId);

    /**
     * Devuelve todos los juegos asociados a una residencia específica.
     *
     * @param idResidencia ID de la residencia.
     * @return Lista de juegos pertenecientes a esa residencia.
     */
    List<Juego> findByResidenciaId(Long idResidencia);

    List<Juego> findByResidencia(Residencia residencia);
}
