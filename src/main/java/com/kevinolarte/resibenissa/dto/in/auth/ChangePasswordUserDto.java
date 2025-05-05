package com.kevinolarte.resibenissa.dto.in.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordUserDto {
    private String oldPassword;
    private String newPassword;
}
