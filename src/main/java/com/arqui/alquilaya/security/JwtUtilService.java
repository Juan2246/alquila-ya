package com.arqui.alquilaya.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio utilitario para la generación, validación y extracción de datos de tokens JWT.
 * JWT (JSON Web Token) es el mecanismo de autenticación stateless que usa la aplicación:
 * después del login, el servidor genera un token que el cliente envía en cada petición.
 * Este servicio se encarga de crear esos tokens y verificar su validez.
 *
 * La clave de firma y la vigencia del token se leen de la configuración
 * (propiedades {@code jwt.secret} y {@code jwt.expiration-ms}), no del código:
 * así cada entorno usa su propia clave sin recompilar.
 */
@Service
public class JwtUtilService {

    // Clave HMAC-SHA256 con la que se firman y verifican los tokens
    private final SecretKey signingKey;

    // Tiempo de validez del token en milisegundos
    private final long tokenValidityMs;

    /**
     * @param secret          clave secreta codificada en Base64 (jwt.secret)
     * @param tokenValidityMs vigencia del token en milisegundos (jwt.expiration-ms)
     */
    public JwtUtilService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long tokenValidityMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.tokenValidityMs = tokenValidityMs;
    }

    /**
     * Extrae todos los claims (datos) contenidos en un token JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsFunction) {
        return claimsFunction.apply(extractAllClaims(token));
    }

    /** Extrae el username (subject) del token JWT. */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extrae la fecha de expiración del token. */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Verifica si el token ya expiró comparando con la fecha actual. */
    public boolean isTokenExpired(String token) {
        Date expiracion = extractExpiration(token);
        return expiracion == null || !expiracion.after(new Date());
    }

    /**
     * Valida un token verificando que no haya expirado y que el username coincida.
     */
    public boolean validateToken(String token, UserSecurity user) {
        String username = extractUsername(token);
        return username != null && (!isTokenExpired(token)) && username.equals(user.getUsername());
    }

    /**
     * Crea un nuevo token JWT con los claims proporcionados, el subject (username),
     * la fecha de emisión y la fecha de expiración.
     */
    private String createToken(String subject, Map<String, Object> claims) {
        return Jwts
                .builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenValidityMs))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * Incluye en los claims: la lista de roles y el ID del usuario.
     */
    public String generateToken(UserSecurity securityUser) {
        Map<String, Object> claims = new HashMap<>();
        Object authorities = securityUser.getAuthorities().stream()
                .map(n -> String.valueOf(n.getAuthority())).toList();
        claims.put("authorities", authorities);
        claims.put("user_id", securityUser.getUser().getId());
        return createToken(securityUser.getUsername(), claims);
    }
}
