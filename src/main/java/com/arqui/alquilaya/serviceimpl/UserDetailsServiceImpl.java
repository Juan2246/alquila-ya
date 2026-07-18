package com.arqui.alquilaya.serviceimpl;

import com.arqui.alquilaya.security.UserSecurity;
import com.arqui.alquilaya.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación de UserDetailsService de Spring Security.
 * Este servicio es invocado automáticamente por Spring Security durante la autenticación.
 * Su responsabilidad es cargar los datos del usuario (incluyendo roles) desde la base de datos
 * y envolverlos en un UserSecurity que Spring Security puede procesar.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserSecurity(userService.findByUsername(username));
    }
}
