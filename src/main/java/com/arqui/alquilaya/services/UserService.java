package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.PerfilDTO;
import com.arqui.alquilaya.dtos.RegistroDTO;
import com.arqui.alquilaya.dtos.UserDTO;
import com.arqui.alquilaya.entities.User;

/**
 * Interfaz de servicio para la gestión de usuarios de autenticación.
 * Maneja registro de nuevos usuarios y búsqueda para login.
 */
public interface UserService {
    PerfilDTO registrar(RegistroDTO registro);
    public User findById(Long id);
    public User findByUsername(String username);
    public UserDTO add(UserDTO userDTO);

    /**
     * Arma el perfil de un usuario autenticado según su rol: si es cliente o
     * propietario, completa los datos personales de ese perfil.
     */
    public PerfilDTO obtenerPerfil(User user);
}