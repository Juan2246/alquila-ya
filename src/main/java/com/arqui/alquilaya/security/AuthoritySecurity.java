package com.arqui.alquilaya.security;

import com.arqui.alquilaya.entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * Adaptador que convierte la entidad Rol del dominio en un GrantedAuthority de Spring Security.
 * Spring Security necesita objetos GrantedAuthority para evaluar permisos;
 * esta clase actúa como puente entre nuestro modelo de datos (Rol) y el framework de seguridad.
 * Es el equivalente de AuthoritySecurity en el proyecto del profesor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthoritySecurity implements GrantedAuthority {

    // Referencia a la entidad Rol de nuestro dominio
    private Rol rol;

    /**
     * Devuelve el nombre del rol (ej: "ROLE_PROPIETARIO") que Spring Security
     * usa para las validaciones de hasAuthority() en SecurityConfiguration.
     */
    @Override
    public String getAuthority() {
        return rol.getNombre();
    }
}
