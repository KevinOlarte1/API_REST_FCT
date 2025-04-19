package com.kevinolarte.resibenissa.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado del envío de correos electrónicos dentro de la aplicación.
 * <p>
 * Este servicio utiliza {@link JavaMailSender} para construir y enviar correos
 * en formato HTML. También incluye una utilidad para validar la estructura de
 * direcciones de correo electrónico.
 * </p>
 *
 * Requiere configuración previa en {@code application.properties} o {@code application.yml}
 * con los datos del servidor SMTP (por ejemplo, Gmail).
 *
 * @author Kevin Olarte
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Verifica si una dirección de correo electrónico tiene un formato válido.
     * <p>
     * La validación se realiza mediante una expresión regular que comprueba la estructura estándar de emails.
     * </p>
     *
     * @param email Dirección de correo electrónico a validar.
     * @return {@code true} si el formato del email es válido, {@code false} en caso contrario.
     */
    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Envía un correo electrónico de verificación en formato HTML.
     * <p>
     * Construye el mensaje a partir de los parámetros recibidos y lo envía utilizando el {@link JavaMailSender}.
     * </p>
     *
     * @param to      Dirección de correo del destinatario.
     * @param subject Asunto del correo.
     * @param text    Cuerpo del mensaje en formato HTML.
     * @throws MessagingException si ocurre un error al crear o enviar el mensaje.
     */
    public void sendVerificationEmail(String to, String subject, String text)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();


        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        mailSender.send(message);
    }

}
