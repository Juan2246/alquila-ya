package com.arqui.alquilaya.config;

import com.arqui.alquilaya.dtos.*;
import com.arqui.alquilaya.entities.*;
import com.arqui.alquilaya.repositories.ComodidadRepository;
import com.arqui.alquilaya.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Carga datos de ejemplo en la base de datos al arrancar la aplicación.
 * Se ejecuta automáticamente (CommandLineRunner) y crea: roles, usuarios,
 * propietarios, clientes, comodidades, propiedades, visitas, reservas,
 * reseñas, favoritos y notificaciones, para poder probar la API desde el primer arranque.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RolService rolService;
    private final UserService userService;
    private final PropietarioService propietarioService;
    private final ClienteService clienteService;
    private final PropiedadService propiedadService;
    private final VisitaService visitaService;
    private final ResenaService resenaService;
    private final FavoritoService favoritoService;
    private final NotificacionService notificacionService;
    private final ReservaService reservaService;
    private final ComodidadRepository comodidadRepository;

    @Override
    public void run(String... args) {

        // ========================================
        // 1. CREAR ROLES DEL SISTEMA
        // ========================================
        log.info("===== Creando Roles =====");
        Rol rolPropietario = rolService.add(new Rol(null, "ROLE_PROPIETARIO", null));
        Rol rolCliente = rolService.add(new Rol(null, "ROLE_CLIENTE", null));
        log.info("Rol creado: {}", rolPropietario.getNombre());
        log.info("Rol creado: {}", rolCliente.getNombre());

        // ========================================
        // 2. CREAR USUARIOS DE AUTENTICACIÓN
        // ========================================
        log.info("===== Creando Usuarios =====");
        UserDTO userProp1 = userService.add(new UserDTO(null, "propietario1", "pass", "ROLE_PROPIETARIO"));
        UserDTO userProp2 = userService.add(new UserDTO(null, "propietario2", "pass", "ROLE_PROPIETARIO"));
        UserDTO userCli1 = userService.add(new UserDTO(null, "cliente1", "pass", "ROLE_CLIENTE"));
        UserDTO userCli2 = userService.add(new UserDTO(null, "cliente2", "pass", "ROLE_CLIENTE"));
        log.info("Usuario creado: propietario1 (id={})", userProp1.getId());
        log.info("Usuario creado: propietario2 (id={})", userProp2.getId());
        log.info("Usuario creado: cliente1 (id={})", userCli1.getId());
        log.info("Usuario creado: cliente2 (id={})", userCli2.getId());

        // ========================================
        // 3. CREAR PERFILES DE PROPIETARIOS
        // ========================================
        log.info("===== Creando Propietarios =====");
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
        log.info("Propietario creado: {} {}", prop1.getNombre(), prop1.getApellido());
        log.info("Propietario creado: {} {}", prop2.getNombre(), prop2.getApellido());

        // ========================================
        // 4. CREAR PERFILES DE CLIENTES
        // ========================================
        log.info("===== Creando Clientes =====");
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
        log.info("Cliente creado: {} {}", cli1.getNombre(), cli1.getApellido());
        log.info("Cliente creado: {} {}", cli2.getNombre(), cli2.getApellido());

        // ========================================
        // 5. CREAR COMODIDADES
        // ========================================
        log.info("===== Creando Comodidades =====");
        Comodidad wifi = comodidadRepository.save(new Comodidad(null, "Wifi", "wifi", null));
        Comodidad tv = comodidadRepository.save(new Comodidad(null, "TV", "tv", null));
        Comodidad cocina = comodidadRepository.save(new Comodidad(null, "Cocina", "kitchen", null));
        Comodidad piscina = comodidadRepository.save(new Comodidad(null, "Piscina", "pool", null));
        Comodidad estacionamiento = comodidadRepository.save(new Comodidad(null, "Estacionamiento", "car", null));
        Comodidad aireAcond = comodidadRepository.save(new Comodidad(null, "Aire Acondicionado", "snowflake", null));
        Comodidad lavadora = comodidadRepository.save(new Comodidad(null, "Lavadora", "washing-machine", null));
        log.info("Comodidades creadas: Wifi, TV, Cocina, Piscina, Estacionamiento, Aire Acondicionado, Lavadora");

        // ========================================
        // 6. CREAR PROPIEDADES CON NUEVOS CAMPOS
        //    (capacidad, latitud, longitud, comodidadIds)
        // ========================================
        log.info("===== Creando Propiedades =====");
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
        log.info("Propiedad creada: {} - S/.{} (cap: 4 huéspedes)", propDTO1.getTitulo(), propDTO1.getPrecio());
        log.info("Propiedad creada: {} - S/.{} (cap: 8 huéspedes)", propDTO2.getTitulo(), propDTO2.getPrecio());
        log.info("Propiedad creada: {} - S/.{} (cap: 2 huéspedes)", propDTO3.getTitulo(), propDTO3.getPrecio());

        // ========================================
        // 7. CREAR VISITAS
        // ========================================
        log.info("===== Creando Visitas =====");
        VisitaDTO visitaDTO1 = visitaService.addDTO(new VisitaDTO(
                null, LocalDateTime.now().minusDays(5).toString(), "COMPLETADA",
                cli1.getId(), propDTO1.getId(), "", ""
        ));
        VisitaDTO visitaDTO2 = visitaService.addDTO(new VisitaDTO(
                null, LocalDateTime.now().plusDays(3).toString(), "PENDIENTE",
                cli2.getId(), propDTO2.getId(), "", ""
        ));
        log.info("Visita creada: Cliente {} → {} [{}]",
                visitaDTO1.getClienteNombre(), visitaDTO1.getPropiedadTitulo(), visitaDTO1.getEstado());
        log.info("Visita creada: Cliente {} → {} [{}]",
                visitaDTO2.getClienteNombre(), visitaDTO2.getPropiedadTitulo(), visitaDTO2.getEstado());

        // ========================================
        // 8. CREAR RESERVAS (sistema de reservas con check-in/check-out)
        // ========================================
        log.info("===== Creando Reservas =====");
        ReservaDTO reservaDTO1 = reservaService.addDTO(new ReservaDTO(
                null,
                LocalDate.now().plusDays(10).toString(),
                LocalDate.now().plusDays(15).toString(),
                null, null,
                cli1.getId(), propDTO1.getId(), "", ""
        ));
        log.info("Reserva creada: {} → {} [{} a {}] Precio total: S/.{}",
                reservaDTO1.getClienteNombre(), reservaDTO1.getPropiedadTitulo(),
                reservaDTO1.getFechaCheckIn(), reservaDTO1.getFechaCheckOut(), reservaDTO1.getPrecioTotal());

        // Crear una reserva COMPLETADA para que cli1 pueda dejar reseña
        ReservaDTO reservaDTO2 = reservaService.addDTO(new ReservaDTO(
                null,
                LocalDate.now().plusDays(30).toString(),
                LocalDate.now().plusDays(33).toString(),
                null, null,
                cli2.getId(), propDTO3.getId(), "", ""
        ));
        log.info("Reserva creada: {} → {} Precio total: S/.{}",
                reservaDTO2.getClienteNombre(), reservaDTO2.getPropiedadTitulo(), reservaDTO2.getPrecioTotal());

        // Marcar la primera reserva como COMPLETADA para probar reseñas
        Reserva reservaCompletada = reservaService.findById(reservaDTO1.getId());
        reservaCompletada.setEstado("COMPLETADA");
        reservaService.update(reservaCompletada);
        log.info("Reserva id={} marcada como COMPLETADA", reservaDTO1.getId());

        // ========================================
        // 9. CREAR RESEÑA DETALLADA (con subcategorías)
        //    Solo posible porque cli1 tiene una RESERVA COMPLETADA en propDTO1
        // ========================================
        log.info("===== Creando Reseña Detallada (con reserva completada) =====");
        ResenaDTO resenaDTO = resenaService.addDTO(new ResenaDTO(
                null, null,  // puntuacion se calcula automáticamente
                5, 4, 5,    // limpieza=5, ubicacion=4, comunicacion=5
                "Excelente departamento, muy bien ubicado y limpio. La comunicación con el propietario fue perfecta.",
                cli1.getId(), propDTO1.getId(), "", ""
        ));
        log.info("Reseña creada: {}", resenaDTO.getComentario());
        log.info("  Puntuación general (promedio): {}/5", resenaDTO.getPuntuacion());
        log.info("  Limpieza: 5 | Ubicación: 4 | Comunicación: 5");

        // ========================================
        // 10. CREAR FAVORITO
        // ========================================
        log.info("===== Creando Favorito =====");
        FavoritoDTO favoritoDTO = favoritoService.addDTO(new FavoritoDTO(
                null, cli1.getId(), propDTO3.getId(), ""
        ));
        log.info("Favorito creado: Cliente {} → {}", cli1.getNombre(), favoritoDTO.getPropiedadTitulo());

        // ========================================
        // 11. CREAR NOTIFICACIÓN
        // ========================================
        log.info("===== Creando Notificación =====");
        Notificacion notificacion = notificacionService.add(new Notificacion(
                null, "Bienvenido a AlquilaYa",
                "Tu cuenta ha sido creada exitosamente. ¡Explora las propiedades disponibles!",
                LocalDateTime.now(), false, cli1
        ));
        log.info("Notificación creada: {}", notificacion.getTitulo());

        log.info("========================================");
        log.info("   AlquilaYa Backend iniciado con éxito");
        log.info("   Servidor: http://localhost:8080/alquilaya");
        log.info("========================================");
    }
}
