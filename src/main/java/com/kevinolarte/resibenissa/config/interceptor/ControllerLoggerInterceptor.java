package com.kevinolarte.resibenissa.config.interceptor;

import com.kevinolarte.resibenissa.LogContext;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class ControllerLoggerInterceptor implements HandlerInterceptor {

    private final LoggerService loggerService;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;

        String endpoint = request.getRequestURI();
        String metodo = request.getMethod();


        // Aquí puedes añadir más lógica para determinar el usuario autenticado
        String descripcion = "Acceso a endpoint";

        loggerService.registrarLog(endpoint, metodo, descripcion);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("Interceptor afterCompletion called");
        LogContext.clear();
    }
}
