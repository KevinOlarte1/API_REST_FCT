package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para acceder y gestionar entidades {@link Residente}.
 * <p>
 * Permite realizar operaciones CRUD y buscar residentes por su residencia.
 *
 * @author Kevin Olarte
 */
@Repository
public interface ResidenteRepository extends JpaRepository<Residente, Long>, JpaSpecificationExecutor<Residente>{


    /**
     * Busca residentes por su documento de identidad.
     * @param docuemntoIdentidad Documento de identidad del residente.
     * @return residente que coincide con el documento de identidad proporcionado.
     */
    Residente findByDocuemntoIdentidad(String docuemntoIdentidad);

}
