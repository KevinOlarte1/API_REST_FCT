package com.kevinolarte.resibenissa.repositories.moduloOrgSalida;

import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoSalidaRepository extends JpaRepository<EventoSalida, Long> {
}
