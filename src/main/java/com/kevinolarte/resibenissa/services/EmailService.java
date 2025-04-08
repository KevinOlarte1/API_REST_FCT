package com.kevinolarte.resibenissa.services;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la gestión de correos electronicos.
 * @Author Kevin William Olarte Braun
 */
@Service
public class EmailService {

    /**
     * Verifica si un correo electrónico tiene un formato válido.
     *
     * @param email El correo electrónico a validar.
     * @return true si el formato es válido, false en caso contrario.
     */
    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

}
