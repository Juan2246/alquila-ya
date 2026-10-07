package com.arqui.alquilaya.security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración central de seguridad de la aplicación AlquilaYa.
 * Define:
 * - Qué rutas son públicas (login, registro, archivos estáticos) y cuáles requieren autenticación.
 * - Qué roles pueden acceder a qué endpoints (PROPIETARIO vs CLIENTE).
 * - El filtro JWT que se ejecuta antes de cada petición.
 * - La política de sesiones (STATELESS porque usamos JWT, no cookies).
 */
@Configuration
public class SecurityConfiguration {

    // Lista de rutas que NO requieren autenticación (acceso libre)
    private static final String[] AUTH_WHITELIST = {
            // Swagger UI para documentación de la API
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",

            // Endpoints de login y registro (deben ser accesibles sin token)
            "/alquilaya/users/login/**",
            "/alquilaya/users/register/**",
            "/alquilaya/users/registro-completo",

            // Archivos estáticos (imágenes subidas)
            "/uploads/propiedades/**",
            "/uploads/perfiles/**",
    };

    /**
     * Bean que define el algoritmo de encriptación para contraseñas.
     * BCrypt es un algoritmo de hashing adaptativo, seguro para contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean que expone el AuthenticationManager de Spring Security.
     * Es necesario para el endpoint de login donde se autentica al usuario.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Aquí se definen todas las reglas de autorización por rol y por endpoint.
     * El filtro JWT llega como parámetro del @Bean: así la configuración no guarda
     * estado propio y Spring resuelve la dependencia al construir la cadena.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {

        // Registrar el filtro JWT ANTES del filtro de autenticación por defecto
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Habilitar CORS y deshabilitar CSRF (no necesario en APIs REST stateless)
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(
                (auth) -> auth
                        // Rutas públicas (sin autenticación)
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers("/uploads/firmas/**").denyAll()

                        // === PROPIEDADES: solo PROPIETARIO puede crear, editar y eliminar ===
                        .requestMatchers(HttpMethod.POST, "/alquilaya/propiedades/**").hasAuthority("ROLE_PROPIETARIO")
                        .requestMatchers(HttpMethod.PUT, "/alquilaya/propiedades/**").hasAuthority("ROLE_PROPIETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/alquilaya/propiedades/**").hasAuthority("ROLE_PROPIETARIO")

                        // === RESEÑAS: solo CLIENTE puede crear reseñas ===
                        .requestMatchers(HttpMethod.POST, "/alquilaya/resenas/**").hasAuthority("ROLE_CLIENTE")

                        // === VISITAS: solo CLIENTE puede crear visitas ===
                        .requestMatchers(HttpMethod.POST, "/alquilaya/visitas/**").hasAuthority("ROLE_CLIENTE")

                        // === RESERVAS: solo CLIENTE puede crear reservas ===
                        .requestMatchers(HttpMethod.POST, "/alquilaya/reservas/**").hasAuthority("ROLE_CLIENTE")

                        // === FAVORITOS: solo CLIENTE puede agregar/quitar favoritos ===
                        .requestMatchers(HttpMethod.POST, "/alquilaya/favoritos/**").hasAuthority("ROLE_CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/alquilaya/favoritos/**").hasAuthority("ROLE_CLIENTE")

                        // === ARCHIVOS: cualquier usuario autenticado puede subir archivos ===
                        .requestMatchers(HttpMethod.POST, "/alquilaya/archivos/**").hasAnyAuthority("ROLE_PROPIETARIO", "ROLE_CLIENTE")

                        // === GETs: accesibles para cualquier usuario autenticado ===
                        .requestMatchers(HttpMethod.GET, "/alquilaya/**").hasAnyAuthority("ROLE_PROPIETARIO", "ROLE_CLIENTE")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
        );

        http.exceptionHandling(errores -> errores
                .authenticationEntryPoint((request, response, error) -> response.sendError(401))
                .accessDeniedHandler((request, response, error) -> response.sendError(403)));

        // Política STATELESS: no se crean sesiones HTTP (todo se maneja con JWT)
        http.sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }
}