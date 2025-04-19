package com.kevinolarte.resibenissa.config;

import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;



    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        //Ver si esa cabeza esta nulla o no es un token bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No token found");
            filterChain.doFilter(request, response);
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
            }

            filterChain.doFilter(request, response);
        }catch(Exception e){
            System.out.println(e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, new ApiException(ApiErrorCode.ENDPOINT_PROTEGIDO));
        }


    }
/*
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/resi/");
    } */
}
