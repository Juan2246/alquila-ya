package com.arqui.alquilaya.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
 */
@Service
public class JwtUtilService {

    // Clave secreta en Base64 para firmar los tokens. Debe mantenerse privada.
    private static final String JWT_SIGNATURE_KEY = "QVJRVUlURUNUVVJBX0FQTElDQUNJT05FU19XRUJfVVBDX0lOR0VOSUVSSUFfU0lTVEVNQVNfREVfSU5GT1JNQUNJT04K";

    // Tiempo de validez del token: 3 horas en milisegundos
    private static final Long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * (long) 3;

    /**
     * Decodifica la clave secreta de Base64 y la convierte en una SecretKey para HMAC-SHA256.
     */
    private SecretKey getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(JWT_SIGNATURE_KEY);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * Extrae todos los claims (datos) contenidos en un token JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
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
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida un token verificando que no haya expirado y que el username coincida.
     */
    public boolean validateToken(String token, UserSecurity user) {
        String username = extractUsername(token);
        return (!isTokenExpired(token)) && (username.equals(user.getUsername()));
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
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
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
