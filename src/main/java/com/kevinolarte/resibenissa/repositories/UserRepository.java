package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar entidades {@link User}.
 * <p>
 * Proporciona métodos personalizados para buscar usuarios por correo electrónico
 * o por la residencia a la que pertenecen, además de los métodos básicos de {@link JpaRepository}.
 *
 * @author Kevin Olarte
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findById(long id);
    /**
     * Obtiene todos los usuarios asociados a una residencia específica.
     *
     * @param id ID de la residencia.
     * @return Lista de usuarios que pertenecen a la residencia.
     */
    List<User> findByBajaFalseAndResidenciaId(Long id);


    List<User> findByBajaTrueAndResidenciaId(Long idResidencia);

    List<User> findByBajaFalse();
    List<User> findByBajaTrue();
}
