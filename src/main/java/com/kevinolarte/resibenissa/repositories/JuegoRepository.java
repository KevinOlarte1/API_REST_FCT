package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    boolean existsByNombreAndResidenciaId(String nombre, Long residenciaId);

    List<Juego> findByResidenciaId(Long idResidencia);
}
