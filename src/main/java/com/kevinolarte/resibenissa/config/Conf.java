package com.kevinolarte.resibenissa.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * Clase de configuración que contiene valores por defecto utilizados en la aplicación.
 * <p>
 * Estos valores suelen emplearse para inicializar entidades, usuarios de sistema, o
 * como fallback en lógica que requiere datos por omisión.
 * </p>
 *
 * <p><b>Nota:</b> Estas constantes no deberían modificarse dinámicamente durante la ejecución.</p>
 *
 * @author Kevin Olarte
 */
public class Conf {
    public static final Long idResidenciaDefault = 1L;
    public static final Long idUsuarioDefault = 1L;
    public static final String emailDefault = "default@default.com";
    public static final String pathPublicAuth= "/auth/";
    public static final String pathPublicSwagger = "/swagger-ui/";

    @Value("${upload.dir}")
    public static String imageResource;
    public static final String imageDefault = "defaultPerfil.png";
}

