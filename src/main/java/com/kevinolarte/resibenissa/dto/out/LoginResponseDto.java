package com.kevinolarte.resibenissa.dto.out;

import com.kevinolarte.resibenissa.models.User;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida utilizado como respuesta al iniciar sesión.
 * <p>
 * Contiene el token JWT generado para el usuario autenticado, así como el tiempo
 * de expiración en milisegundos, indicando cuánto tiempo es válido el token.
 * </p>
 *
 * Este DTO es devuelto por el endpoint de autenticación {@code /auth/login}.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private Long expiresIn;
    private Long idUser;
    private Long idResidencia;

    public LoginResponseDto(String token, long expiresIn, User user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.idUser = user.getId();
        this.idResidencia = user.getResidencia().getId();
    }
}
