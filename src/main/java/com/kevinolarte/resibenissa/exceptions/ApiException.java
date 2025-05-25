package com.kevinolarte.resibenissa.exceptions;

import com.kevinolarte.resibenissa.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {
   private ResiException resiException;
   private User user;

    public ApiException(ResiException resiException, User user) {
        super(resiException.getMessage());
        this.resiException = resiException;
        this.user = user;
    }
}
