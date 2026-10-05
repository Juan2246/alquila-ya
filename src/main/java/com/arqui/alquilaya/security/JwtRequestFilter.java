package com.arqui.alquilaya.security;

import jakarta.servlet.FilterChain;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta CADA petición HTTP para verificar si contiene un token JWT válido.
 * Se ejecuta antes del filtro estándar de Spring Security (UsernamePasswordAuthenticationFilter).
 *
 * Flujo:
 * 1. Lee el header "Authorization" buscando "Bearer <token>"
 * 2. Si existe, extrae el username del token
 * 3. Carga los datos del usuario y valida el token
 * 4. Si es válido, establece la autenticación en el SecurityContext
 * 5. Si el token es inválido se responde 401; sin token decide la política de la ruta
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtUtilService jwtUtilService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            try {
                String token = cabecera.substring(7);
                String username = jwtUtilService.extractUsername(token);
                if (username == null || username.isBlank()) {
                    throw new UsernameNotFoundException("Sesión no válida");
                }
                UserSecurity usuario = (UserSecurity) userDetailsService.loadUserByUsername(username);
                if (!usuario.isEnabled() || !jwtUtilService.validateToken(token, usuario)) {
                    throw new UsernameNotFoundException("Sesión no válida");
                }
                var autenticacion = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        // Paso 5: Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
}
