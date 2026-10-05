package com.arqui.alquilaya;

import com.arqui.alquilaya.config.RolesIniciales;
import com.arqui.alquilaya.repositories.UserRepository;
import com.arqui.alquilaya.repositories.RolRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AlquilayaApplicationTests extends EntornoDePrueba {
    @Autowired UserRepository usuarios;
    @Autowired RolRepository roles;
    @Autowired RolesIniciales inicializador;

    @Test
    void arrancaSinCuentasDemoYLosRolesSonIdempotentes() {
        assertThat(usuarios.count()).isZero();
        assertThat(roles.findAll()).extracting("nombre")
                .containsExactlyInAnyOrder("ROLE_CLIENTE", "ROLE_PROPIETARIO");
        inicializador.run();
        assertThat(roles.count()).isEqualTo(2);
        assertThat(usuarios.count()).isZero();
    }
}
