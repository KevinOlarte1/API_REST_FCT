package com.kevinolarte.resibenissa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuración del servicio de envío de correos electrónicos.
 * <p>
 * Esta clase define un {@link JavaMailSender} que se configura automáticamente
 * con los datos definidos en el archivo {@code application.properties} o {@code application.yml}.
 * Utiliza los servidores de Gmail como proveedor SMTP.
 * </p>
 *
 * <p>
 * Las credenciales y configuraciones sensibles se inyectan desde las propiedades:
 * <ul>
 *   <li><code>spring.mail.username</code></li>
 *   <li><code>spring.mail.password</code></li>
 * </ul>
 * </p>
 *
 * @return Bean de {@link JavaMailSender} listo para ser inyectado y utilizado en servicios.
 *
 * @author Kevin Olarte
 */
@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /**
     * Crea e inicializa el {@link JavaMailSender} con configuración para SMTP (Gmail).
     *
     * @return Instancia de {@link JavaMailSender} configurada.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
