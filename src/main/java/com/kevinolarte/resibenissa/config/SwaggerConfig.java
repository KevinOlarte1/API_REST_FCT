package com.kevinolarte.resibenissa.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de tu proyecto")
                        .description("Documentación generada automáticamente por Swagger con SpringDoc")
                        .version("1.0.0")
                        .contact(new Contact().name("Tu Nombre").email("tuemail@ejemplo.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación externa")
                        .url("https://ejemplo.com")
                );
    }
}
