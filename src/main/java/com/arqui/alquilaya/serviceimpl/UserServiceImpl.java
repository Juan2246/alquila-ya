package com.arqui.alquilaya.serviceimpl;

import com.arqui.alquilaya.dtos.UserDTO;
import com.arqui.alquilaya.entities.Rol;
import com.arqui.alquilaya.entities.User;
import com.arqui.alquilaya.repositories.UserRepository;
import com.arqui.alquilaya.services.RolService;
import com.arqui.alquilaya.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementación del servicio de usuarios para autenticación.
 * Se encarga de crear nuevos usuarios encriptando su contraseña con BCrypt
 * y asignándoles los roles correspondientes.
 * Replica exactamente el patrón de UserServiceImpl del proyecto del profesor.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolService rolService;

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
}
