package com.arqui.alquilaya.security;

import com.arqui.alquilaya.entities.Rol;
import com.arqui.alquilaya.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Adaptador que convierte nuestra entidad User en un UserDetails de Spring Security.
 * Spring Security requiere un objeto UserDetails para la autenticación y autorización.
 * Esta clase envuelve al User del dominio y expone sus datos (username, password, roles)
 * en el formato que Spring Security necesita. Es clave para el flujo JWT.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSecurity implements UserDetails {

    // Referencia a la entidad User de nuestro dominio
    private User user;

    /**
     * Convierte la lista de Roles del usuario en una colección de GrantedAuthority.
     * Cada Rol se envuelve en un AuthoritySecurity para cumplir con la interfaz de Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().map(AuthoritySecurity::new).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Indica si la cuenta está habilitada. Se lee del campo 'enabled' de la entidad User.
     */
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getEnabled());
    }
}
