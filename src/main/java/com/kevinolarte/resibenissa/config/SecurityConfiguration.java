package com.kevinolarte.resibenissa.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {


    /**
     * Congigura la cadena de filtros de seguridad para la aplicación.
     * <p>
     * En esta configuración se desactiva CSF y se permite el acceso a todas las rutas
     * @param http Objeto HttpSecurity utilizado para construir la configuración de seguridad.
     * @return La cadena de filtros de seguridad configurada
     * @throws Exception Si ocurre un error al formar la cadena de filtros.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}
