package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.services.impl.UserDetailsServiceImpl;
import com.arqui.alquilaya.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad User.
 * se usa en UserDetailsServiceImpl para cargar el usuario por su nombre.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);
}
