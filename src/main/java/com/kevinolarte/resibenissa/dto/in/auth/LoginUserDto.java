package com.kevinolarte.resibenissa.dto.in.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferecia de datos (DTO) utilizado para logearse y obtener el token de un usario activado anteriormente.
 * <p>
 * Este DTO permite obtener acceso a los demas endPoint porque se usara para crear un token. A partir de los parametros que tiene.
 *
 * @author Kevin Olarte
 */
@Getter
@Setter
public class LoginUserDto {

    private String email;
    private String password;


}
