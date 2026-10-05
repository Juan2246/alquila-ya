package com.arqui.alquilaya.config;

import com.arqui.alquilaya.entities.Rol;
import com.arqui.alquilaya.repositories.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Los roles son configuración de la aplicación; no dependen de las cuentas demo. */
@Component
@Order(0)
@RequiredArgsConstructor
public class RolesIniciales implements CommandLineRunner {
    private final RolRepository roles;

    @Override
    @Transactional
    public void run(String... args) {
        for (String nombre : new String[]{"ROLE_CLIENTE", "ROLE_PROPIETARIO"}) {
            if (roles.findByNombre(nombre) == null) {
                roles.save(new Rol(null, nombre, null));
            }
        }
    }
}
