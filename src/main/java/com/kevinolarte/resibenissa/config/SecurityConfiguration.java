package com.kevinolarte.resibenissa.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Clase de configuración de seguridad para la API.
 * <p>
 * Define la cadena de filtros de seguridad, política de sesiones,
 * rutas públicas y configuración CORS para permitir solicitudes seguras desde el frontend.
 * </p>
 *
 * Esta configuración se basa en JWT y es stateless (sin sesiones de servidor).
 *
 * @author Kevin
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    /**
     * Configura la cadena de filtros de seguridad para la aplicación.
     * <p>
     * - Desactiva CSRF.<br>
     * - Permite acceso libre a rutas que comienzan con {@code /auth/**}.<br>
     * - Requiere autenticación para cualquier otra petición.<br>
     * - Aplica política de sesión stateless (usada con JWT).<br>
     * - Agrega el filtro personalizado para validar JWT antes de {@link UsernamePasswordAuthenticationFilter}.
     * </p>
     *
     * @param http Objeto {@link HttpSecurity} para construir la configuración.
     * @return Cadena de filtros de seguridad {@link SecurityFilterChain} configurada.
     * @throws Exception Si ocurre un error en la construcción de la cadena.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(Conf.PATH_PUBLIC_AUTH + "**").permitAll()
                        .requestMatchers(Conf.PATH_PUBLIC_SWAGGER + "**").permitAll()
                        .requestMatchers(Conf.PATH_PUBLIC_RESI_CONTROLLER + "**").permitAll()
                        .requestMatchers(Conf.PATH_PUBLIC_RESI_GET).permitAll()




                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
