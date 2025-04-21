package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.User;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida para representar los datos públicos de un usuario del sistema.
 * <p>
 * Esta clase se utiliza para devolver información básica del usuario en las respuestas
 * de la API, sin incluir detalles sensibles como contraseñas o tokens de autenticación.
 * </p>
 *
 * <p>
 * Contiene datos como el ID del usuario, nombre, email, estado de activación y la residencia asociada.
 * </p>
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private boolean enabled;
    private Long idResidencia;
    private String fotoPerfil;


    public UserResponseDto(User user) {
        this.id = user.getId();
        this.nombre = user.getNombre();
        this.apellido = user.getApellido();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.idResidencia = user.getResidencia().getId();
        this.fotoPerfil = user.getFotoPerfil();
    }


}
