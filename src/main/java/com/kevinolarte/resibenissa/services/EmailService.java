package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residente;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
@AllArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;
    private JwtService jwtService;

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
    public void sendEmail(String to, String subject, String text) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();


        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        mailSender.send(message);
    }

    public void sendNotificationParticipante(ParticipanteResponseDto participanteDto) {
        try {

            Map<String, Object> claims = new HashMap<>();
            claims.put("idParticipante", participanteDto.getId());
            claims.put("idEvento", participanteDto.getIdEvento());
            claims.put("idResidencia", participanteDto.getIdResidencia());

            String token = jwtService.generateTokenConExpiracionCustomClaims(claims, Duration.ofMinutes(30));
            System.out.println("Token: " + token);
            // Construir URLs
            String urlPermitir = "http://localhost:8080/public/allowParticipante?token=" + token;
            String urlRechazar = "http://localhost:8080/public/denyParticipante?token=" + token;

            // Leer la plantilla HTML desde resources/templates
            Path htmlPath = Paths.get("src/main/resources/templates/permiso-excursion.html");
            String html = Files.readString(htmlPath);

            // Reemplazar los placeholders
            html = html.replace("{{nombreFamiliar}}", "Familiar")
                    .replace("{{nombreResidente}}", "Residente " + participanteDto.getIdResidente())
                    .replace("{{nombreExcursion}}", "Excursión especial")
                    .replace("{{fecha}}", LocalDate.now().plusDays(7).toString()) // ejemplo de fecha
                    .replace("{{urlPermitir}}", urlPermitir)
                    .replace("{{urlRechazar}}", urlRechazar);


            sendEmail(participanteDto.getFamiliar1(), "Permiso para excursión", html);
            if (participanteDto.getFamiliar2() != null) {
                sendEmail(participanteDto.getFamiliar2(), "Permiso para excursión", html);
            }

        } catch (Exception e) {
            throw new ApiException(ApiErrorCode.ERROR_MAIL_SENDER);
        }
    }

}
