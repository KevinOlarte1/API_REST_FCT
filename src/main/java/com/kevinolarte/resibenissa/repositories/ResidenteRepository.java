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
     * Obtiene todos los residentes asociados a una residencia específica.
     *
     * @param residencia La entidad {@link Residencia} de la cual se desean obtener los residentes.
     * @return Lista de residentes que pertenecen a la residencia dada.
     */
    List<Residente> findByResidencia(Residencia residencia);


    /**
     * Busca residentes por su documento de identidad.
     * @param docuemntoIdentidad Documento de identidad del residente.
     * @return residente que coincide con el documento de identidad proporcionado.
     */
    Residente findByDocuemntoIdentidad(String docuemntoIdentidad);

    List<Residente> findByResidenciaAndDocuemntoIdentidad(Residencia residencia, String docuemntoIdentidad);
}
