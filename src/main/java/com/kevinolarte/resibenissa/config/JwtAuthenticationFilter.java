package com.kevinolarte.resibenissa.config;

import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.JwtService;
import com.kevinolarte.resibenissa.services.LoggerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Filtro de autenticación que intercepta todas las peticiones HTTP y verifica si contienen
 * un token JWT válido en la cabecera {@code Authorization}.
 * <p>
 * Si el token es válido, autentica al usuario y lo registra en el {@link SecurityContextHolder}.
 * En caso de error, delega la excepción al {@link HandlerExceptionResolver} para devolver
 * una respuesta estructurada.
 * </p>
 *
 * Este filtro se ejecuta una sola vez por petición, al extender de {@link OncePerRequestFilter}.
 *
 * @author Kevin
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final LoggerService loggerService;


    /**
     * Lógica principal del filtro. Verifica si la petición contiene un token JWT válido
     * y, si es así, autentica al usuario en el contexto de seguridad de Spring.
     *
     * @param request       Petición HTTP entrante.
     * @param response      Respuesta HTTP saliente.
     * @param filterChain   Cadena de filtros que se continúa tras el procesamiento.
     * @throws ServletException si ocurre un error en el filtro.
     * @throws IOException      si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String endpoint = request.getRequestURI();
        String metodo = request.getMethod();


        // Aquí puedes añadir más lógica para determinar el usuario autenticado
        String descripcion = "Acceso a endpoint";

        loggerService.registrarLog(endpoint, metodo, descripcion);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    new ApiException(new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO), "Falta el token de autorización.")
            );
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extrtractEmail(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            String uri = request.getRequestURI();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (uri.startsWith("/admin/resi") && auth != null && auth.isAuthenticated()) {
                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (!isAdmin) {
                    handlerExceptionResolver.resolveException(
                            request,
                            response,
                            null,
                            new ApiException(new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO), "Falta el token de autorización.")
                    );
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch(Exception e){
            handlerExceptionResolver.resolveException(request, response, null, new ApiException(new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO), e.getMessage()));
        }



    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        System.out.println("URI: " + path);
        return path.startsWith(Conf.PATH_PUBLIC_SWAGGER)
                || path.startsWith(Conf.PATH_PUBLIC_AUTH)
                || path.startsWith(Conf.PATH_PUBLIC_RESI_GET)
                || path.startsWith(Conf.PATH_PUBLIC_RESI_CONTROLLER);
    }
}
