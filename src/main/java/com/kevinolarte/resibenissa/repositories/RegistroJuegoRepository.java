package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.RegistroJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroJuegoRepository extends JpaRepository<RegistroJuego, Long> {

    List<RegistroJuego> findByResidente_Residencia_Id(Long idResidencia);
    List<RegistroJuego> findByJuegoId(Long idJuego);
    List<RegistroJuego> findByResidenteId(Long idResidencia);

    List<RegistroJuego> findByFecha(LocalDate fecha);
}
