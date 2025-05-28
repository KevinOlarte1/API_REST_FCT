package com.kevinolarte.resibenissa.config;

import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.JwtService;
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
        boolean exceptionLanzada = false;
        System.out.println("Ejecutando el filtro de JWT");
        final String authHeader = request.getHeader("Authorization");
        System.out.println("Bienvenida al filtro de JWT");
        //Ver si esa cabeza esta nulla o no es un token bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No hay token");
            handlerExceptionResolver.resolveException(request, response, null, new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO));
            System.out.println("Pasando al siguiente filtro");
            return;
        }
        try{
            //Sacamos el token
            final String jwt = authHeader.substring(7);
            //Sacamos el email del mismo token
            final String userEmail = jwtService.extrtractEmail(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //Tenemos que tener el correo y no tener la authorizacion para dale la authorizacion
            if (userEmail != null && authentication == null){

                //Obtenemos ese userDetail para ver si exsite, busca por correo, configurado en la AplicationConfiguration
                UserDetails userDetails =this.userDetailsService.loadUserByUsername(userEmail);
                //Comprobamos si el usuario es admin.
                System.out.println(request.getRequestURI());
                if(request.getRequestURI().matches("^/admin/resi/.*")){

                    //Si el token es de un usuario normal, lanzamos la exception
                    User user = (User) userDetails;
                    if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){

                        exceptionLanzada = true;
                        handlerExceptionResolver.resolveException(request, response, null, new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO));
                        filterChain.doFilter(request, response);
                        return;


                    }
                }

                //Comprobamos si ese token es de ese usario y no esta expirado
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    //Damos authorizacion
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                else {
                    //Si no es valido, lanzamos la exception
                    handlerExceptionResolver.resolveException(request, response, null, new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO));
                }
            }

            filterChain.doFilter(request, response);
        } catch(Exception e){
            System.out.println(e.getMessage());
            if (!exceptionLanzada)
                handlerExceptionResolver.resolveException(request, response, null, new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO));

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
