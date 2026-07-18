package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.PerfilDTO;
import com.arqui.alquilaya.dtos.TokenDTO;
import com.arqui.alquilaya.dtos.UserDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.entities.User;
import com.arqui.alquilaya.security.JwtUtilService;
import com.arqui.alquilaya.security.UserSecurity;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.PropietarioService;
import com.arqui.alquilaya.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtilService jwtUtilService;

    @Autowired
    ClienteService clienteService;

    @Autowired
    PropietarioService propietarioService;


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

        // Determinar el rol del usuario
        String rol = user.getRoles().stream()
                .map(r -> r.getNombre())
                .collect(Collectors.joining(";"));

        PerfilDTO perfil = new PerfilDTO();
        perfil.setUserId(user.getId());
        perfil.setUsername(user.getUsername());
        perfil.setRol(rol);

        // Buscar el perfil según el rol
        if (rol.contains("ROLE_CLIENTE")) {
            Cliente cliente = clienteService.findByUserId(user.getId());
            if (cliente != null) {
                perfil.setPerfilId(cliente.getId());
                perfil.setNombre(cliente.getNombre());
                perfil.setApellido(cliente.getApellido());
                perfil.setDni(cliente.getDni());
                perfil.setCorreo(cliente.getCorreo());
                perfil.setEdad(cliente.getEdad());
                perfil.setFoto(cliente.getFoto());
                perfil.setDescripcion(cliente.getDescripcion());
            }
        } else if (rol.contains("ROLE_PROPIETARIO")) {
            Propietario propietario = propietarioService.findByUserId(user.getId());
            if (propietario != null) {
                perfil.setPerfilId(propietario.getId());
                perfil.setNombre(propietario.getNombre());
                perfil.setApellido(propietario.getApellido());
                perfil.setDni(propietario.getDni());
                perfil.setCorreo(propietario.getCorreo());
                perfil.setEdad(propietario.getEdad());
                perfil.setFoto(propietario.getFoto());
                perfil.setDescripcion(propietario.getObservaciones());
            }
        }

        return new ResponseEntity<>(perfil, HttpStatus.OK);
    }
}
