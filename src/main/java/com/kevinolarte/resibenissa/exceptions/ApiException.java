package com.kevinolarte.resibenissa.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada para representar errores controlados en la lógica de negocio de la aplicación.
 * <p>
 * Esta excepción encapsula un {@link ApiErrorCode}, el cual contiene:
 * <ul>
 *   <li>Un mensaje de error legible.</li>
 *   <li>Un código HTTP asociado.</li>
 *   <li>Un código interno único para identificar el tipo de error.</li>
 * </ul>
 *
 * Al lanzar esta excepción, los controladores o filtros pueden capturarla y devolver una respuesta estructurada
 * al cliente con la información correspondiente.
 *
 * @see ApiErrorCode
 * @author Kevin Olarte
 */
@Getter
public class ApiException extends RuntimeException{

    private final ApiErrorCode errorCode;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return errorCode.getHttpStatus().value();
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
