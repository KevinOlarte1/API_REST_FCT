package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio encargado de la generación, validación y extracción de información de tokens JWT.
 * <p>
 * Utiliza la clave secreta configurada en las propiedades de la aplicación y respeta el tiempo
 * de expiración establecido para los tokens. Este servicio también añade el email como un claim
 * adicional en los tokens generados.
 * </p>
 *
 * <p>
 * El campo {@code sub} del token corresponde al {@code username}, pero en esta aplicación se trabaja principalmente con {@code email}.
 * </p>
 *
 * @author Kevin Olarte
 */
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Extrae el nombre de usuario (username/subject) del token JWT.
     *
     * @param token Token JWT.
     * @return Nombre de usuario extraído del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim personalizado del token, utilizando una función de resolución.
     *
     * @param token Token JWT.
     * @param claimsResolver Función que define qué claim se desea extraer.
     * @return Valor del claim.
     * @param <T> Tipo del claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para un usuario, sin claims adicionales.
     *
     * @param userDetails Usuario autenticado.
     * @return Token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {

        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims personalizados y el correo del usuario.
     *
     * @param extraClaims Claims adicionales a incluir en el token.
     * @param userDetails Usuario autenticado.
     * @return Token JWT generado.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        //Un poco feo pero necesario. No uso username. Bean Aplication dice que buscamos por correo.
        if (userDetails instanceof User user) {
            extraClaims.put("email", user.getEmail());
        }
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Devuelve el tiempo de expiración configurado para los tokens (en milisegundos).
     *
     * @return Tiempo de expiración en milisegundos.
     */
    public long getExpirationTime() {
        return this.jwtExpiration;
    }

    /**
     * Construye el token JWT firmándolo con la clave secreta.
     *
     * @param extraClaims Claims adicionales.
     * @param userDetails Datos del usuario.
     * @param expirationTime Tiempo de expiración en milisegundos.
     * @return Token JWT generado.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expirationTime) {

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSingKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * Extrae el claim personalizado {@code email} del token.
     *
     * @param token Token JWT.
     * @return Email del usuario.
     */
    public String extrtractEmail(String token) {
        //Del extra claim que pusimos.
        return extractAllClaims(token).get("email", String.class);
    }

    /**
     * Verifica si un token es válido para un usuario dado.
     *
     * @param token Token JWT.
     * @param userDetails Usuario autenticado.
     * @return {@code true} si el token es válido y no ha expirado.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Verifica si el token ya ha expirado.
     *
     * @param token Token JWT.
     * @return {@code true} si el token está expirado.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    /**
     * Extrae la fecha de expiración del token.
     *
     * @param token Token JWT.
     * @return Fecha de expiración.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Extrae todos los claims del token.
     *
     * @param token Token JWT.
     * @return Todos los claims contenidos en el token.
     */
    private  Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * Obtiene la clave secreta firmada a partir del string base64 en la configuración.
     *
     * @return Clave HMAC SHA válida para firmar/verificar JWT.
     */
    private Key getSingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
