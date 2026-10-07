package com.arqui.alquilaya.config;

import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.entities.Reserva;
import com.arqui.alquilaya.entities.User;
import com.arqui.alquilaya.repositories.ClienteRepository;
import com.arqui.alquilaya.repositories.PropiedadRepository;
import com.arqui.alquilaya.repositories.PropietarioRepository;
import com.arqui.alquilaya.repositories.ReservaRepository;
import com.arqui.alquilaya.repositories.RolRepository;
import com.arqui.alquilaya.repositories.UserRepository;
import com.arqui.alquilaya.entities.*;
import com.arqui.alquilaya.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Datos ficticios y opcionales; nunca se cargan en el arranque normal. */
@Component
@Profile("demo")
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository usuarios;
    private final RolRepository roles;
    private final ClienteRepository clientes;
    private final PropietarioRepository propietarios;
    private final PropiedadRepository propiedades;
    private final ReservaRepository reservas;
    private final ContratoRepository contratos;
    private final PasswordEncoder encoder;

    @Value("${demo.propietario.password:}")
    private String clavePropietario;
    @Value("${demo.cliente.password:}")
    private String claveCliente;

    @Override
    @Transactional
    public void run(String... args) {
        // Una base con cuentas existentes se conserva íntegra.
        if (usuarios.count() > 0) return;
        if (clavePropietario.isBlank() || claveCliente.isBlank()) {
            throw new IllegalStateException("El perfil demo requiere DEMO_OWNER_PASSWORD y DEMO_CLIENT_PASSWORD");
        }
        User usuarioPropietario = crearUsuario("propietario.demo@example.invalid", clavePropietario, "ROLE_PROPIETARIO");
        User usuarioCliente = crearUsuario("cliente.demo@example.invalid", claveCliente, "ROLE_CLIENTE");
        Propietario propietario = new Propietario();
        propietario.setNombre("Propietario");
        propietario.setApellido("Demo");
        propietario.setCorreo(usuarioPropietario.getUsername());
        propietario.setUser(usuarioPropietario);
        propietarios.save(propietario);
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente");
        cliente.setApellido("Demo");
        cliente.setCorreo(usuarioCliente.getUsername());
        cliente.setUser(usuarioCliente);
        clientes.save(cliente);
        Propiedad propiedad = new Propiedad();
        propiedad.setTitulo("Departamento de demostración");
        propiedad.setDescripcion("Datos ficticios para probar la API localmente.");
        propiedad.setDistrito("Lima");
        propiedad.setPrecio(BigDecimal.valueOf(100));
        propiedad.setHabitaciones(2);
        propiedad.setCapacidad(4);
        propiedad.setFechaPublicacion(LocalDateTime.now());
        propiedad.setPropietario(propietario);
        propiedades.save(propiedad);
        Reserva reserva = reservas.save(new Reserva(null, LocalDate.now().plusDays(10), LocalDate.now().plusDays(13),
                "PENDIENTE", BigDecimal.valueOf(300), LocalDateTime.now(), cliente, propiedad));
        Contrato contrato = new Contrato();
        contrato.setReserva(reserva); contrato.setCliente(cliente); contrato.setPropiedad(propiedad);
        contrato.setFechaInicio(reserva.getFechaCheckIn().atStartOfDay());
        contrato.setFechaFin(reserva.getFechaCheckOut().atStartOfDay());
        contrato.setEstado("PENDIENTE");
        contratos.save(contrato);
    }

    private User crearUsuario(String nombre, String clave, String rol) {
        return usuarios.save(new User(null, nombre, encoder.encode(clave), true,
                List.of(roles.findByNombre(rol))));
    }
}
