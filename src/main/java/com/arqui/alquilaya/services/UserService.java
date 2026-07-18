package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.UserDTO;
import com.arqui.alquilaya.entities.User;

/**
 * Interfaz de servicio para la gestión de usuarios de autenticación.
 * Maneja registro de nuevos usuarios y búsqueda para login.
 */
public interface UserService {
    public User findById(Long id);
    public User findByUsername(String username);
    public UserDTO add(UserDTO userDTO);
}
