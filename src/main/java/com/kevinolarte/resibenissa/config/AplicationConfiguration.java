package com.kevinolarte.resibenissa.config;

import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import com.kevinolarte.resibenissa.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuración de seguridad de la aplicación.
 * <p>
 * Define los beans necesarios para la autenticación de usuarios,
 * incluyendo el codificador de contraseñas, el proveedor de autenticación
 * y el servicio de obtención de detalles de usuario.
 *
 * @author Kevin Olarte
 */
@Configuration
@AllArgsConstructor
public class AplicationConfiguration {

    private final UserRepository userRepository;

    /**
     * Devuelve un UserDetailsService que busca usuarios por email en la base de datos.
     *
     * @return Implementación de UserDetailsService personalizada.
     * @throws UsernameNotFoundException si el usuario no existe.
     */
    @Bean
    UserDetailsService userDetailsService(){
    return email -> {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new ResiException(ApiErrorCode.USUARIO_INVALIDO);
        }
        if (user.isBaja()){
            throw new ResiException(ApiErrorCode.USUARIO_BAJA);
        }
        return user;
    };
    }
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Crea un bean de {@link AuthenticationProvider} personalizado que usa
     * el {@link UserDetailsService} y el codificador de contraseñas definidos.
     *
     * @return el proveedor de autenticación configurado.
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }



}
