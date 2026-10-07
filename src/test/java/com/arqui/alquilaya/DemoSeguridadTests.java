package com.arqui.alquilaya;

import com.arqui.alquilaya.config.DataSeeder;
import com.arqui.alquilaya.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class DemoSeguridadTests {
    private final UserRepository usuarios = mock(UserRepository.class);
    private final RolRepository roles = mock(RolRepository.class);
    private final ClienteRepository clientes = mock(ClienteRepository.class);
    private final PropietarioRepository propietarios = mock(PropietarioRepository.class);
    private final PropiedadRepository propiedades = mock(PropiedadRepository.class);
    private final ReservaRepository reservas = mock(ReservaRepository.class);
    private final ContratoRepository contratos = mock(ContratoRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final DataSeeder demo = new DataSeeder(usuarios, roles, clientes, propietarios, propiedades, reservas, contratos, encoder);

    @Test
    void demoSinClavesNoCreaNingunDato() {
        setField(demo, "clavePropietario", "");
        setField(demo, "claveCliente", "");
        assertThatThrownBy(() -> demo.run()).isInstanceOf(IllegalStateException.class);
        verify(usuarios, never()).save(any());
        verifyNoInteractions(roles, clientes, propietarios, propiedades, reservas, contratos, encoder);
    }

    @Test
    void demoConBaseExistenteConservaLasCuentasYDatos() {
        when(usuarios.count()).thenReturn(1L);
        demo.run();
        verify(usuarios, never()).save(any());
        verifyNoInteractions(roles, clientes, propietarios, propiedades, reservas, contratos, encoder);
    }
}
