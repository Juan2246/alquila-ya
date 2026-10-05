package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.PerfilDTO;
import com.arqui.alquilaya.dtos.TokenDTO;
import com.arqui.alquilaya.dtos.UserDTO;
import com.arqui.alquilaya.entities.User;
import com.arqui.alquilaya.security.JwtUtilService;
import com.arqui.alquilaya.security.UserSecurity;
import com.arqui.alquilaya.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtilService jwtUtilService;



    @PostMapping("/users/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO user) {
        user = userService.add(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @PostMapping("/users/login")
    public ResponseEntity<TokenDTO> login(@RequestBody User user) {
        // Paso 1: Verificar credenciales (lanza excepción si son incorrectas)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        // Paso 2: Cargar datos completos del usuario autenticado
        UserSecurity userSecurity = (UserSecurity) userDetailsService.loadUserByUsername(user.getUsername());

        // Paso 3: Generar token JWT
        String jwt = jwtUtilService.generateToken(userSecurity);
        Long id = userSecurity.getUser().getId();

        // Paso 4: Concatenar roles como string separado por ";"
        String roles = userSecurity.getUser().getRoles().stream()
                .map(rol -> rol.getNombre())
                .collect(Collectors.joining(";", "", ""));

        return new ResponseEntity<>(new TokenDTO(jwt, id, roles), HttpStatus.OK);
    }

    /**
     * Perfil dinámico: devuelve toda la información del usuario autenticado y su rol.
     * El frontend solo necesita enviar su token JWT en el header Authorization.
     * El backend extrae el user_id del SecurityContext y busca el perfil asociado.
     */
    @GetMapping("/users/perfil")
    public ResponseEntity<PerfilDTO> getPerfil() {
        // Obtener el usuario autenticado del SecurityContext
        UserSecurity userSecurity = (UserSecurity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userSecurity.getUser();

        PerfilDTO perfil = userService.obtenerPerfil(user);

        return new ResponseEntity<>(perfil, HttpStatus.OK);
    }
}
