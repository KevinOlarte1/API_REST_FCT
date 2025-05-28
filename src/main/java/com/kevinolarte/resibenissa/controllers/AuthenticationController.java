package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.auth.LoginUserDto;
import com.kevinolarte.resibenissa.dto.in.auth.RegisterUserDto;
import com.kevinolarte.resibenissa.dto.in.auth.VerifyUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.dto.out.LoginResponseDto;
import com.kevinolarte.resibenissa.services.AuthenticationService;
import com.kevinolarte.resibenissa.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la creacion de usuarios
 * <p>
 * Expone endpoints relacionados con el registro, inicio de sesión,
 * verificación de cuenta por correo y reenvío de códigos de verificación.
 * </p>
 *
 * Todas las rutas están bajo el prefijo <code>/auth</code>.
 *
 * @author Kevin
 */
@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;


    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * El usuario creado se guarda con estado "no activado" hasta que complete
     * el proceso de verificación vía código enviado al correo.
     * </p>
     *
     * @param registerUserDto Datos necesarios para registrar al usuario.
     * @return {@link ResponseEntity} con los datos del usuario registrado.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegisterUserDto registerUserDto){
        UserResponseDto user;
        try{
            user = authenticationService.singUp(registerUserDto);
        } catch (ResiException e){
            throw new ApiException(e, e.getMessage());
        }catch (Exception e){
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }

        return ResponseEntity.ok(user);

    }

    /**
     * Autentica a un usuario existente y devuelve un token JWT válido.
     *
     * @param loginUserDto DTO con email y contraseña.
     * @return {@link ResponseEntity} con el token JWT y su tiempo de expiración.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto){
        LoginResponseDto loginResponse;
        try{
            User userauthentication = authenticationService.authenticate(loginUserDto);
            String token = jwtService.generateToken(userauthentication);
            loginResponse = new LoginResponseDto(
                    token,
                    jwtService.getExpirationTime(),
                    userauthentication
            );
        }catch (ResiException e){
            throw new ApiException(e, e.getMessage());
        }catch (Exception e){
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Verifica un usuario usando el código enviado por correo.
     *
     * @param verifyUserDto DTO con email y código de verificación.
     * @return {@link ResponseEntity} con mensaje de éxito.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try{
            authenticationService.verifyUser(verifyUserDto);
        }catch (ResiException e){
            throw new ApiException(e, e.getMessage());
        }catch (Exception e){
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }

        return ResponseEntity.ok("AcountVerfied");

    }

    /**
     * Reenvía un nuevo código de verificación al correo indicado.
     *
     * @param email Dirección de correo del usuario.
     * @return {@link ResponseEntity} con mensaje de confirmación.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendVerificationCode(@RequestParam String email ){
        try{
            authenticationService.resendVerificationCode(email);
        }catch (ResiException e){
            throw new ApiException(e, e.getMessage());
        }catch (Exception e){
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
        return ResponseEntity.ok("Verification Code Resent");

    }
}
