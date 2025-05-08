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
    public static final String PATH_PUBLIC_AUTH= "/auth/";
    public static final String PATH_PUBLIC_SWAGGER = "/swagger-ui/";
    public static final String PATH_PUBLIC_RESI_GET = "/resi/getAll";

    @Value("${upload.dir}")
    public static String imageResource;
    public static final String imageDefault = "defaultPerfil.png";
}

