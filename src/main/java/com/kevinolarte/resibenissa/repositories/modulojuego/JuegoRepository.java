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
     * Busca si existe juegos por su nombre, ignorando mayúsculas y minúsculas.
     * @param nombre Nombre del juego a buscar.
     * @return Lista de juegos que coinciden con el nombre dado.
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Busca juegos cuyo nombre contenga una cadena específica, ignorando mayúsculas y minúsculas.
     * @param nombre Cadena a buscar en el nombre del juego.
     * @return Lista de juegos que contienen la cadena en su nombre.
     */
    List<Juego> findByNombreContainingIgnoreCase(String nombre);
}
