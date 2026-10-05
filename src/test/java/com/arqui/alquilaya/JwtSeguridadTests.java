package com.arqui.alquilaya;

import com.arqui.alquilaya.entities.User;
import com.arqui.alquilaya.security.JwtUtilService;
import com.arqui.alquilaya.security.UserSecurity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtSeguridadTests {
    private final byte[] bytes = nuevaClave();
    private final String clave = Base64.getEncoder().encodeToString(bytes);
    private final JwtUtilService jwt = new JwtUtilService(clave, 60_000);
    private final UserSecurity usuario = new UserSecurity(new User(1L, "prueba@example.invalid", null, true, List.of()));

    private static byte[] nuevaClave() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    @Test
    void claveVaciaODebilNoPermiteConstruirElServicio() {
        assertThatThrownBy(() -> new JwtUtilService("", 60_000)).isInstanceOf(WeakKeyException.class);
        assertThatThrownBy(() -> new JwtUtilService(Base64.getEncoder().encodeToString(new byte[8]), 60_000))
                .isInstanceOf(WeakKeyException.class);
    }

    @Test
    void tokenVencidoSeRechaza() {
        String token = Jwts.builder().subject(usuario.getUsername()).expiration(new Date(0))
                .signWith(Keys.hmacShaKeyFor(bytes)).compact();
        assertThatThrownBy(() -> jwt.validateToken(token, usuario)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void tokenSinIdentidadOSinVencimientoNoEsValido() {
        String sinIdentidad = Jwts.builder().expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(bytes)).compact();
        String sinVencimiento = Jwts.builder().subject(usuario.getUsername())
                .signWith(Keys.hmacShaKeyFor(bytes)).compact();
        assertThat(jwt.validateToken(sinIdentidad, usuario)).isFalse();
        assertThat(jwt.validateToken(sinVencimiento, usuario)).isFalse();
    }
}
