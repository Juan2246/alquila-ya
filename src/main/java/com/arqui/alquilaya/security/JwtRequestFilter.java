package com.arqui.alquilaya.security;

import jakarta.servlet.FilterChain;
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
 * 5. Si no hay token o es inválido, la petición continúa sin autenticación
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtUtilService jwtUtilService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Paso 1: Obtener el header Authorization de la petición HTTP
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        // Paso 2: Verificar si el header tiene formato "Bearer <token>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Extraer solo el token (sin "Bearer ")
            username = jwtUtilService.extractUsername(token);
        }

        // Paso 3: Si hay username y no hay autenticación previa, validar el token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserSecurity securityUser = (UserSecurity) this.userDetailsService.loadUserByUsername(username);

            // Paso 4: Si el token es válido, establecer la autenticación en el contexto de seguridad
            if (jwtUtilService.validateToken(token, securityUser)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        securityUser, null, securityUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // Paso 5: Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
}
