package com.kevinolarte.resibenissa.exceptions;

import com.kevinolarte.resibenissa.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {
   private ResiException resiException;
   private String mensaje;

    public ApiException(ResiException resiException, User user) {
        super(resiException.getMessage());
        this.resiException = resiException;
        this.mensaje =  resiException.getMessage() + " - Usuario: " + user.getUsername();
    }
    public ApiException(ResiException resiException, User user, String mensaje) {
        super(resiException.getMessage());
        this.resiException = resiException;
        this.mensaje =  mensaje + " - Usuario: " + user.getUsername();
    }

    public ApiException(ResiException resiException, String mensaje) {
        super(resiException.getMessage());
        this.resiException = resiException;
        this.mensaje = mensaje;
    }
}
