package com.arqui.alquilaya;

import com.arqui.alquilaya.dtos.*;
import com.arqui.alquilaya.entities.*;
import com.arqui.alquilaya.repositories.ComodidadRepository;
import com.arqui.alquilaya.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Clase principal de la aplicación AlquilaYa.
 * El Bean startConfiguration (CommandLineRunner) se ejecuta automáticamente al iniciar la app
 * y carga datos de prueba en la base de datos para facilitar el testing.
 */
@SpringBootApplication
public class AlquilayaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlquilayaApplication.class, args);
    }

    /**
     * CommandLineRunner que carga datos iniciales al arrancar la aplicación.
     * Crea: roles, usuarios, propietarios, clientes, comodidades, propiedades,
     * visitas, reservas, reseñas, favoritos y notificaciones de ejemplo.
     */
    @Bean
    public CommandLineRunner startConfiguration(
            RolService rolService,
            UserService userService,
            PropietarioService propietarioService,
            ClienteService clienteService,
            PropiedadService propiedadService,
            VisitaService visitaService,
            ResenaService resenaService,
            FavoritoService favoritoService,
            NotificacionService notificacionService,
            ReservaService reservaService,
            ComodidadRepository comodidadRepository
    ) {
        return args -> {

            // ========================================
            // 1. CREAR ROLES DEL SISTEMA
            // ========================================
            System.out.println("\n===== Creando Roles =====");
            Rol rolPropietario = rolService.add(new Rol(null, "ROLE_PROPIETARIO", null));
            Rol rolCliente = rolService.add(new Rol(null, "ROLE_CLIENTE", null));
            System.out.println("Rol creado: " + rolPropietario.getNombre());
            System.out.println("Rol creado: " + rolCliente.getNombre());

            // ========================================
            // 2. CREAR USUARIOS DE AUTENTICACIÓN
            // ========================================
            System.out.println("\n===== Creando Usuarios =====");
            UserDTO userProp1 = userService.add(new UserDTO(null, "propietario1", "pass", "ROLE_PROPIETARIO"));
            UserDTO userProp2 = userService.add(new UserDTO(null, "propietario2", "pass", "ROLE_PROPIETARIO"));
            UserDTO userCli1 = userService.add(new UserDTO(null, "cliente1", "pass", "ROLE_CLIENTE"));
            UserDTO userCli2 = userService.add(new UserDTO(null, "cliente2", "pass", "ROLE_CLIENTE"));
            System.out.println("Usuario creado: propietario1 (id=" + userProp1.getId() + ")");
            System.out.println("Usuario creado: propietario2 (id=" + userProp2.getId() + ")");
            System.out.println("Usuario creado: cliente1 (id=" + userCli1.getId() + ")");
            System.out.println("Usuario creado: cliente2 (id=" + userCli2.getId() + ")");

            // ========================================
            // 3. CREAR PERFILES DE PROPIETARIOS
            // ========================================
            System.out.println("\n===== Creando Propietarios =====");
            User user1 = userService.findById(userProp1.getId());
            Propietario prop1 = propietarioService.add(new Propietario(
                    null, "Carlos", "Mendoza", "12345678", "carlos@email.com",
                    45, null, "Propietario con 3 inmuebles en Lima", user1, null
            ));

            User user2 = userService.findById(userProp2.getId());
            Propietario prop2 = propietarioService.add(new Propietario(
                    null, "María", "Gonzales", "87654321", "maria@email.com",
                    38, null, "Propietaria de departamentos en Miraflores", user2, null
            ));
            System.out.println("Propietario creado: " + prop1.getNombre() + " " + prop1.getApellido());
            System.out.println("Propietario creado: " + prop2.getNombre() + " " + prop2.getApellido());

            // ========================================
            // 4. CREAR PERFILES DE CLIENTES
            // ========================================
            System.out.println("\n===== Creando Clientes =====");
            User user3 = userService.findById(userCli1.getId());
            Cliente cli1 = clienteService.add(new Cliente(
                    null, "Ana", "Torres", "11223344", "ana@email.com",
                    28, null, "Busco departamento amoblado", user3,
                    null, null, null, null, null, null
            ));

            User user4 = userService.findById(userCli2.getId());
            Cliente cli2 = clienteService.add(new Cliente(
                    null, "Pedro", "Ramírez", "55667788", "pedro@email.com",
                    32, null, "Busco casa para familia", user4,
                    null, null, null, null, null, null
            ));
            System.out.println("Cliente creado: " + cli1.getNombre() + " " + cli1.getApellido());
            System.out.println("Cliente creado: " + cli2.getNombre() + " " + cli2.getApellido());

            // ========================================
            // 5. CREAR COMODIDADES
            // ========================================
            System.out.println("\n===== Creando Comodidades =====");
            Comodidad wifi = comodidadRepository.save(new Comodidad(null, "Wifi", "wifi", null));
            Comodidad tv = comodidadRepository.save(new Comodidad(null, "TV", "tv", null));
            Comodidad cocina = comodidadRepository.save(new Comodidad(null, "Cocina", "kitchen", null));
            Comodidad piscina = comodidadRepository.save(new Comodidad(null, "Piscina", "pool", null));
            Comodidad estacionamiento = comodidadRepository.save(new Comodidad(null, "Estacionamiento", "car", null));
            Comodidad aireAcond = comodidadRepository.save(new Comodidad(null, "Aire Acondicionado", "snowflake", null));
            Comodidad lavadora = comodidadRepository.save(new Comodidad(null, "Lavadora", "washing-machine", null));
            System.out.println("Comodidades creadas: Wifi, TV, Cocina, Piscina, Estacionamiento, Aire Acondicionado, Lavadora");

            // ========================================
            // 6. CREAR PROPIEDADES CON NUEVOS CAMPOS
            //    (capacidad, latitud, longitud, comodidadIds)
            // ========================================
            System.out.println("\n===== Creando Propiedades =====");
            PropiedadDTO propDTO1 = propiedadService.addDTO(new PropiedadDTO(
                    null, "Departamento moderno en Miraflores", "2 dormitorios, vista al mar",
                    "Av. Larco 123", "Miraflores", new BigDecimal("2500.00"), 2,
                    4, -12.1197, -77.0300,  // capacidad=4, coordenadas de Miraflores
                    prop1.getId(), "",
                    Arrays.asList(wifi.getId(), tv.getId(), cocina.getId(), aireAcond.getId())
            ));
            PropiedadDTO propDTO2 = propiedadService.addDTO(new PropiedadDTO(
                    null, "Casa amplia en San Borja", "4 dormitorios, jardín grande",
                    "Calle Las Flores 456", "San Borja", new BigDecimal("4500.00"), 4,
                    8, -12.1067, -76.9975,  // capacidad=8, coordenadas de San Borja
                    prop1.getId(), "",
                    Arrays.asList(wifi.getId(), tv.getId(), cocina.getId(), piscina.getId(), estacionamiento.getId(), lavadora.getId())
            ));
            PropiedadDTO propDTO3 = propiedadService.addDTO(new PropiedadDTO(
                    null, "Estudio en Barranco", "1 dormitorio, cerca al malecón",
                    "Jr. Unión 789", "Barranco", new BigDecimal("1200.00"), 1,
                    2, -12.1480, -77.0227,  // capacidad=2, coordenadas de Barranco
                    prop2.getId(), "",
                    Arrays.asList(wifi.getId(), tv.getId())
            ));
            System.out.println("Propiedad creada: " + propDTO1.getTitulo() + " - S/." + propDTO1.getPrecio() + " (cap: 4 huéspedes)");
            System.out.println("Propiedad creada: " + propDTO2.getTitulo() + " - S/." + propDTO2.getPrecio() + " (cap: 8 huéspedes)");
            System.out.println("Propiedad creada: " + propDTO3.getTitulo() + " - S/." + propDTO3.getPrecio() + " (cap: 2 huéspedes)");

            // ========================================
            // 7. CREAR VISITAS
            // ========================================
            System.out.println("\n===== Creando Visitas =====");
            VisitaDTO visitaDTO1 = visitaService.addDTO(new VisitaDTO(
                    null, LocalDateTime.now().minusDays(5).toString(), "COMPLETADA",
                    cli1.getId(), propDTO1.getId(), "", ""
            ));
            VisitaDTO visitaDTO2 = visitaService.addDTO(new VisitaDTO(
                    null, LocalDateTime.now().plusDays(3).toString(), "PENDIENTE",
                    cli2.getId(), propDTO2.getId(), "", ""
            ));
            System.out.println("Visita creada: Cliente " + visitaDTO1.getClienteNombre()
                    + " → " + visitaDTO1.getPropiedadTitulo() + " [" + visitaDTO1.getEstado() + "]");
            System.out.println("Visita creada: Cliente " + visitaDTO2.getClienteNombre()
                    + " → " + visitaDTO2.getPropiedadTitulo() + " [" + visitaDTO2.getEstado() + "]");

            // ========================================
            // 8. CREAR RESERVAS (sistema de reservas con check-in/check-out)
            // ========================================
            System.out.println("\n===== Creando Reservas =====");
            ReservaDTO reservaDTO1 = reservaService.addDTO(new ReservaDTO(
                    null,
                    LocalDate.now().plusDays(10).toString(),
                    LocalDate.now().plusDays(15).toString(),
                    null, null,
                    cli1.getId(), propDTO1.getId(), "", ""
            ));
            System.out.println("Reserva creada: " + reservaDTO1.getClienteNombre()
                    + " → " + reservaDTO1.getPropiedadTitulo()
                    + " [" + reservaDTO1.getFechaCheckIn() + " a " + reservaDTO1.getFechaCheckOut() + "]"
                    + " Precio total: S/." + reservaDTO1.getPrecioTotal());

            // Crear una reserva COMPLETADA para que cli1 pueda dejar reseña
            ReservaDTO reservaDTO2 = reservaService.addDTO(new ReservaDTO(
                    null,
                    LocalDate.now().plusDays(30).toString(),
                    LocalDate.now().plusDays(33).toString(),
                    null, null,
                    cli2.getId(), propDTO3.getId(), "", ""
            ));
            System.out.println("Reserva creada: " + reservaDTO2.getClienteNombre()
                    + " → " + reservaDTO2.getPropiedadTitulo()
                    + " Precio total: S/." + reservaDTO2.getPrecioTotal());

            // Marcar la primera reserva como COMPLETADA para probar reseñas
            Reserva reservaCompletada = reservaService.findById(reservaDTO1.getId());
            reservaCompletada.setEstado("COMPLETADA");
            reservaService.update(reservaCompletada);
            System.out.println("Reserva id=" + reservaDTO1.getId() + " marcada como COMPLETADA");

            // ========================================
            // 9. CREAR RESEÑA DETALLADA (con subcategorías)
            //    Solo posible porque cli1 tiene una RESERVA COMPLETADA en propDTO1
            // ========================================
            System.out.println("\n===== Creando Reseña Detallada (con reserva completada) =====");
            ResenaDTO resenaDTO = resenaService.addDTO(new ResenaDTO(
                    null, null,  // puntuacion se calcula automáticamente
                    5, 4, 5,    // limpieza=5, ubicacion=4, comunicacion=5
                    "Excelente departamento, muy bien ubicado y limpio. La comunicación con el propietario fue perfecta.",
                    cli1.getId(), propDTO1.getId(), "", ""
            ));
            System.out.println("Reseña creada: " + resenaDTO.getComentario());
            System.out.println("  Puntuación general (promedio): " + resenaDTO.getPuntuacion() + "/5");
            System.out.println("  Limpieza: 5 | Ubicación: 4 | Comunicación: 5");

            // ========================================
            // 10. CREAR FAVORITO
            // ========================================
            System.out.println("\n===== Creando Favorito =====");
            FavoritoDTO favoritoDTO = favoritoService.addDTO(new FavoritoDTO(
                    null, cli1.getId(), propDTO3.getId(), ""
            ));
            System.out.println("Favorito creado: Cliente " + cli1.getNombre()
                    + " → " + favoritoDTO.getPropiedadTitulo());

            // ========================================
            // 11. CREAR NOTIFICACIÓN
            // ========================================
            System.out.println("\n===== Creando Notificación =====");
            Notificacion notificacion = notificacionService.add(new Notificacion(
                    null, "Bienvenido a AlquilaYa",
                    "Tu cuenta ha sido creada exitosamente. ¡Explora las propiedades disponibles!",
                    LocalDateTime.now(), false, cli1
            ));
            System.out.println("Notificación creada: " + notificacion.getTitulo());

            System.out.println("\n========================================");
            System.out.println("   AlquilaYa Backend iniciado con éxito");
            System.out.println("   Servidor: http://localhost:8080/alquilaya");
            System.out.println("========================================");

        };
    }
}
