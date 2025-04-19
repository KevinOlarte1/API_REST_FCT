package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * DTO de salida que representa la información pública de una residencia.
 * <p>
 * Esta clase se utiliza para enviar al cliente los datos esenciales de una residencia,
 * incluyendo su nombre, email y los identificadores de usuarios y residentes asociados.
 * </p>
 *
 * <p>
 * Se construye a partir de una instancia de {@link Residencia}, mapeando únicamente los IDs
 * de las relaciones con usuarios y residentes para evitar sobrecarga de datos en la respuesta.
 * </p>
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class ResidenciaResponseDto {
    private Long id;
    private String nombre;
    private String email;
    private List<Long> usuarios;
    private List<Long> residentes;

    public ResidenciaResponseDto(Residencia residencia) {
        this.id = residencia.getId();
        this.nombre = residencia.getNombre();
        this.email = residencia.getEmail();

        this.usuarios = residencia.getUsuarios()
                .stream()
                .map(User::getId)
                .toList();

        this.residentes = residencia.getResidentes()
                .stream()
                .map(Residente::getId)
                .toList();
    }

}


