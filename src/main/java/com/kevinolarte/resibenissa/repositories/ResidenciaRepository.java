package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.Residencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder y gestionar entidades {@link Residencia}.
 * <p>
 * Proporciona métodos personalizados para buscar residencias por nombre o correo electrónico,
 * además de los métodos estándar de {@link JpaRepository}.
 *
 * @author Kevin Olarte
 */
@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, Long> {



    /**
     * Busca una residencia por su correo electrónico.
     *
     * @param email Correo electrónico de la residencia.
     * @return Un Optional con la residencia si se encuentra.
     */
    Optional<Residencia> findByEmail(String email);

    /**
     * Busca una residencia por su nombre.
     *
     * @param nombre Nombre de la residencia.
     * @return Un Optional con la residencia si se encuentra.
     */
    Optional<Residencia> findByNombre(String nombre);

    /**
     * Busca residencias que estén marcadas como dadas de baja.
     *
     * @return Lista de residencias que tienen el campo baja en true.
     */
    List<Residencia> findByBajaTrue();
}
