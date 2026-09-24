package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.PerfilDTO;
import com.arqui.alquilaya.dtos.UserDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.entities.Rol;
import com.arqui.alquilaya.entities.User;
import com.arqui.alquilaya.repositories.UserRepository;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.PropietarioService;
import com.arqui.alquilaya.services.RolService;
import com.arqui.alquilaya.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de usuarios para autenticación.
 * Se encarga de crear nuevos usuarios encriptando su contraseña con BCrypt
 * y asignándoles los roles correspondientes.
 * Replica exactamente el patrón de UserServiceImpl del proyecto del profesor.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RolService rolService;

    private final ClienteService clienteService;

    private final PropietarioService propietarioService;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Convierte una cadena de roles separados por ";" en una lista de entidades Rol.
     * Por ejemplo: "ROLE_PROPIETARIO;ROLE_CLIENTE" → [Rol(ROLE_PROPIETARIO), Rol(ROLE_CLIENTE)]
     */
    private List<Rol> rolesFromString(String roles) {
        List<Rol> rolList = new ArrayList<>();
        List<String> rolStringList = Arrays.stream(roles.split(";")).toList();
        for (String rolString : rolStringList) {
            Rol rol = rolService.findByNombre(rolString);
            if (rol != null) {
                rolList.add(rol);
            }
        }
        return rolList;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 1. Convierte los roles de texto a entidades Rol.
     * 2. Encripta la contraseña con BCrypt antes de guardar.
     * 3. Crea la entidad User con enabled=true.
     * 4. Guarda en BD y retorna el DTO con el ID asignado.
     */
    @Override
    public UserDTO add(UserDTO userDTO) {
        List<Rol> rolList = rolesFromString(userDTO.getRoles());

        User newUser = new User(null, userDTO.getUsername(),
                new BCryptPasswordEncoder().encode(userDTO.getPassword()),
                true, rolList);

        newUser = userRepository.save(newUser);
        userDTO.setId(newUser.getId());
        return userDTO;
    }

    /**
     * Perfil dinámico: devuelve la información del usuario y de su perfil
     * (cliente o propietario) según el rol que tenga.
     */
    @Override
    public PerfilDTO obtenerPerfil(User user) {
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

        return perfil;
    }
}
