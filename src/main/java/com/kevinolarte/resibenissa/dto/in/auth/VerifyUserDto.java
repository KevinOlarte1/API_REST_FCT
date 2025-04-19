package com.kevinolarte.resibenissa.dto.in.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Objecto de transferencia de datos (DTO) utilizado para verificar el usuario creado anteriormente
 * <p>
 * Este DTO permite actibar el usuario creado anteriormete, con solo pasando
 * los siguientes parametros.
 */
@Getter
@Setter
public class VerifyUserDto {
    private String email;
    private String verificationCode;

}
