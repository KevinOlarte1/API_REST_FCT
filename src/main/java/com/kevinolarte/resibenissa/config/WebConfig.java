package com.kevinolarte.resibenissa.config;

import com.kevinolarte.resibenissa.config.interceptor.ControllerLoggerInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ControllerLoggerInterceptor controllerLoggerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(controllerLoggerInterceptor)
                .addPathPatterns("/**"); // Intercepta todos los endpoints
    }
}

