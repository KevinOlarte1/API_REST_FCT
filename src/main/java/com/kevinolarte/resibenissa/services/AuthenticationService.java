package com.kevinolarte.resibenissa.services;

import ch.qos.logback.core.model.INamedModel;
import com.kevinolarte.resibenissa.config.Conf;
import com.kevinolarte.resibenissa.dto.in.auth.LoginUserDto;
import com.kevinolarte.resibenissa.dto.in.auth.RegisterUserDto;
import com.kevinolarte.resibenissa.dto.in.auth.VerifyUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * Servicio encargado de gestionar la autenticación y verificación de usuarios.
 * <p>
 * Incluye el registro de nuevos usuarios, generación y envío de códigos de verificación por correo electrónico,
 * autenticación mediante email y contraseña, y validación del código de activación.
 * </p>
 *
 * Este servicio utiliza:
 * <ul>
 *   <li>{@link PasswordEncoder} para cifrar contraseñas.</li>
 *   <li>{@link AuthenticationManager} para autenticación en Spring Security.</li>
 *   <li>{@link EmailService} para el envío de correos electrónicos.</li>
 *   <li>{@link ResidenciaService} para validar residencias al registrar usuarios.</li>
 * </ul>
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ResidenciaService residenciaService;

    /**
     * Registra un nuevo usuario si no existe previamente.
     * <p>
     * Genera un código de verificación y lo envía por correo electrónico.
     * El usuario se crea con estado desactivado hasta completar la verificación.
     * </p>
     *
     * @param input DTO con los datos del usuario a registrar.
     * @return DTO con los datos del usuario creado.
     * @throws ApiException si el email es inválido, ya existe, o la residencia no es válida.
     */
    public UserResponseDto singUp(RegisterUserDto input){
        if (input.getEmail() == null || input.getEmail().trim().isEmpty() || input.getPassword() == null || input.getPassword().trim().isEmpty()
            || input.getIdResidencia() == null || input.getNombre() == null || input.getNombre().trim().isEmpty() || input.getApellido() == null || input.getApellido().trim().isEmpty()){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        input.setEmail(input.getEmail().trim().toLowerCase());
        if (!EmailService.isEmailValid(input.getEmail())){
            throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
        }

        //Miramos si ese usuario y residencia existen
        Optional<User> userTest =  userRepository.findByEmail(input.getEmail());
        Residencia residenciaTest = residenciaService.findById(input.getIdResidencia());
        if(userTest.isPresent()){
            throw new ApiException(ApiErrorCode.USER_EXIST);
        }
        if (residenciaTest == null) {
            throw new ApiException(ApiErrorCode.RESIDENCIA_INVALIDO);
        }

        User user = new User(input.getNombre(), input.getApellido(),input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        user.setResidencia(residenciaTest);
        user.setFotoPerfil("/uploads/" + Conf.imageDefault);
        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser);
    }

    /**
     * Autentica un usuario existente mediante email y contraseña.
     *
     * @param input DTO con credenciales de acceso.
     * @return El objeto {@link User} autenticado.
     * @throws ApiException si el usuario no existe o no está activado.
     */
    public User authenticate(LoginUserDto input){
        if (input.getEmail() == null || input.getEmail().trim().isEmpty() || input.getPassword() == null || input.getPassword().trim().isEmpty()){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        input.setEmail(input.getEmail().trim().toLowerCase());
        if (!EmailService.isEmailValid(input.getEmail())){
            throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
        }

        //Ver si ese usuario existe o no
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new ApiException(ApiErrorCode.USUARIO_INVALIDO));

        //Ver si esta activado
        if(!user.isEnabled()){
            throw new ApiException(ApiErrorCode.USER_NO_ACTIVADO);
        }

        //Autehnticamos
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            throw new ApiException(ApiErrorCode.CONTRASENA_INCORRECTA);
        }

        return user;
    }

    /**
     * Verifica el código enviado por correo y activa la cuenta del usuario.
     *
     * @param input DTO que contiene el email y el código de verificación.
     * @throws ApiException si el código está expirado, es inválido, o el usuario no existe.
     */
    public void verifyUser(VerifyUserDto input){
        if (input.getEmail() == null || input.getEmail().trim().isEmpty() || input.getVerificationCode() == null || input.getVerificationCode().trim().isEmpty()){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();


            if(user.getVerificationExpiration().isBefore(LocalDateTime.now())){
                throw new ApiException(ApiErrorCode.CODIGO_EXPIRADO);
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())){
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationExpiration(null);
                userRepository.save(user); //CUIDADO!!!
            }else{
                throw new ApiException(ApiErrorCode.CODIGO_INVALIDO);
            }
        }
        else{
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }
    }

    /**
     * Reenvía un nuevo código de verificación por correo si el usuario aún no está activado.
     *
     * @param email Dirección de correo del usuario.
     * @throws ApiException si el usuario no existe o ya está activado.
     */
    public void resendVerificationCode(String email){
        if (email == null || email.trim().isEmpty()){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        email = email.trim().toLowerCase();
        if (!EmailService.isEmailValid(email)){
            throw new ApiException(ApiErrorCode.CORREO_INVALIDO);
        }
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.isEnabled()){
                throw new ApiException(ApiErrorCode.USER_YA_ACTIVADO);
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiration(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        }
        else{
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }
    }

    /**
     * Genera un código de verificación de 6 dígitos.
     *
     * @return Código de verificación como String.
     */
    public String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * Envía un correo con el código de verificación al usuario.
     *
     * @param user Usuario al que se le enviará el correo.
     * @throws ApiException si ocurre un error al enviar el correo.
     */
    public void sendVerificationEmail(User user){
        String subject = "Account verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try{
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        }catch (MessagingException e){
            throw new ApiException(ApiErrorCode.ERROR_MAIL_SENDER);

        }
    }
}
