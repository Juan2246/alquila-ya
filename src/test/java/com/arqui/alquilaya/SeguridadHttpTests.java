package com.arqui.alquilaya;

import com.arqui.alquilaya.dtos.UserDTO;
import com.arqui.alquilaya.entities.*;
import com.arqui.alquilaya.repositories.*;
import com.arqui.alquilaya.security.JwtUtilService;
import com.arqui.alquilaya.security.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcPrint;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Peticiones reales por la cadena de filtros: identidades y datos aislados por prueba. */
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@ActiveProfiles("test")
@Transactional
class SeguridadHttpTests extends EntornoDePrueba {
    @Autowired MockMvc http;
    @Autowired ObjectMapper json;
    @Autowired UserRepository usuarios;
    @Autowired RolRepository roles;
    @Autowired ClienteRepository clientes;
    @Autowired PropietarioRepository propietarios;
    @Autowired PropiedadRepository propiedades;
    @Autowired ReservaRepository reservas;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtUtilService jwt;

    User usuarioCliente, otroCliente, usuarioDueno, otroDueno;
    Cliente cliente, ajeno;
    Propietario dueno, duenoAjeno;
    Propiedad propiedad;
    Reserva reserva;
    String clave;

    @BeforeEach
    void preparar() {
        clave = UUID.randomUUID().toString();
        usuarioCliente = cuenta("cliente", "ROLE_CLIENTE");
        otroCliente = cuenta("otro-cliente", "ROLE_CLIENTE");
        usuarioDueno = cuenta("dueno", "ROLE_PROPIETARIO");
        otroDueno = cuenta("otro-dueno", "ROLE_PROPIETARIO");
        cliente = perfilCliente(usuarioCliente);
        ajeno = perfilCliente(otroCliente);
        dueno = perfilDueno(usuarioDueno);
        duenoAjeno = perfilDueno(otroDueno);
        propiedad = new Propiedad();
        propiedad.setTitulo("Propiedad ficticia");
        propiedad.setPrecio(BigDecimal.valueOf(100));
        propiedad.setPropietario(dueno);
        propiedades.save(propiedad);
        reserva = reservas.save(new Reserva(null, LocalDate.now().plusDays(10), LocalDate.now().plusDays(12),
                "PENDIENTE", BigDecimal.valueOf(200), LocalDateTime.now(), cliente, propiedad));
    }

    private User cuenta(String nombre, String rol) {
        return usuarios.save(new User(null, nombre + "@example.invalid", encoder.encode(clave), true,
                List.of(roles.findByNombre(rol))));
    }

    private Cliente perfilCliente(User usuario) {
        Cliente perfil = new Cliente();
        perfil.setNombre("Cliente de prueba");
        perfil.setUser(usuario);
        return clientes.save(perfil);
    }

    private Propietario perfilDueno(User usuario) {
        Propietario perfil = new Propietario();
        perfil.setNombre("Propietario de prueba");
        perfil.setUser(usuario);
        return propietarios.save(perfil);
    }

    private MockHttpServletRequestBuilder como(MockHttpServletRequestBuilder peticion, User usuario) {
        return peticion.header("Authorization", "Bearer " + jwt.generateToken(new UserSecurity(usuario)));
    }

    private MockHttpServletRequestBuilder cuerpo(MockHttpServletRequestBuilder peticion, Object datos) {
        return peticion.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(datos));
    }

    private Map<String, Object> nuevaReserva(Long clienteId) {
        return Map.of("clienteId", clienteId, "propiedadId", propiedad.getId(),
                "fechaCheckIn", LocalDate.now().plusDays(20).toString(),
                "fechaCheckOut", LocalDate.now().plusDays(23).toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ROLE_ADMIN", "ROLE_CLIENTE;ROLE_PROPIETARIO", "ROLE_CLIENTE;ROLE_ADMIN", "cliente", " ROLE_CLIENTE"})
    void registroRechazaRolesNoPermitidos(String rol) throws Exception {
        long antes = usuarios.count();
        Map<String, Object> entrada = new HashMap<>();
        entrada.put("username", "nueva@example.invalid");
        entrada.put("password", clave);
        entrada.put("roles", rol);
        http.perform(cuerpo(post("/alquilaya/users/register"), entrada)).andExpect(status().isBadRequest());
        assertThat(usuarios.count()).isEqualTo(antes);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_CLIENTE", "ROLE_PROPIETARIO"})
    void registroAceptaUnRolYNoDevuelveLaClave(String rol) throws Exception {
        http.perform(cuerpo(post("/alquilaya/users/register"), Map.of("username", "nueva@example.invalid",
                        "password", clave, "roles", rol)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.password").doesNotExist());
        User nueva = usuarios.findByUsername("nueva@example.invalid");
        assertThat(encoder.matches(clave, nueva.getPassword())).isTrue();
        assertThat(nueva.getRoles()).extracting(Rol::getNombre).containsExactly(rol);
    }

    @Test
    void loginSigueRecibiendoClaveSinSerializarla() throws Exception {
        http.perform(cuerpo(post("/alquilaya/users/login"), Map.of("username", usuarioCliente.getUsername(), "password", clave)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.jwtToken").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist());
        UserDTO entrada = new UserDTO(null, "prueba", clave, "ROLE_CLIENTE");
        assertThat(json.readTree(json.writeValueAsString(entrada)).has("password")).isFalse();
        assertThat(entrada.toString()).doesNotContain(clave);
        assertThat(usuarioCliente.toString()).doesNotContain(usuarioCliente.getPassword());
    }

    @Test
    void respuestasAnidadasNuncaIncluyenHash() throws Exception {
        for (String ruta : List.of("/propiedades", "/propiedades/" + propiedad.getId(), "/reservas/" + reserva.getId())) {
            String respuesta = http.perform(como(get("/alquilaya" + ruta), usuarioCliente))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            assertThat(respuesta).doesNotContain("\"password\"", usuarioCliente.getPassword(), usuarioDueno.getPassword());
        }
    }

    @Test
    void soloElClienteVeSuLista() throws Exception {
        String ruta = "/alquilaya/reservas/cliente/" + cliente.getId();
        http.perform(como(get(ruta), usuarioCliente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        http.perform(como(get(ruta), otroCliente)).andExpect(status().isForbidden());
        http.perform(como(get(ruta), usuarioDueno)).andExpect(status().isForbidden());
    }

    @Test
    void soloLosParticipantesVenElDetalle() throws Exception {
        String ruta = "/alquilaya/reservas/" + reserva.getId();
        http.perform(como(get(ruta), usuarioCliente)).andExpect(status().isOk());
        http.perform(como(get(ruta), usuarioDueno)).andExpect(status().isOk());
        http.perform(como(get(ruta), otroCliente)).andExpect(status().isForbidden());
        http.perform(como(get(ruta), otroDueno)).andExpect(status().isForbidden());
    }

    @Test
    void soloElDuenoVeLasReservasDelInmueble() throws Exception {
        String ruta = "/alquilaya/reservas/propiedad/" + propiedad.getId();
        http.perform(como(get(ruta), usuarioDueno)).andExpect(status().isOk());
        http.perform(como(get(ruta), usuarioCliente)).andExpect(status().isForbidden());
        http.perform(como(get(ruta), otroDueno)).andExpect(status().isForbidden());
    }

    @Test
    void disponibilidadSoloExponeFechasYEstadoActivo() throws Exception {
        reservas.save(new Reserva(null, LocalDate.now().plusDays(15), LocalDate.now().plusDays(16),
                "CANCELADA", BigDecimal.TEN, LocalDateTime.now(), ajeno, propiedad));
        String salida = http.perform(como(get("/alquilaya/reservas/propiedad/" + propiedad.getId() + "/disponibilidad"), otroCliente))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(salida).get(0).propertyNames())
                .containsExactlyInAnyOrder("fechaCheckIn", "fechaCheckOut", "estado");
    }

    @Test
    void crearReservaAjenaSeRechazaSinEscribir() throws Exception {
        long antes = reservas.count();
        http.perform(como(cuerpo(post("/alquilaya/reservas"), nuevaReserva(ajeno.getId())), usuarioCliente))
                .andExpect(status().isForbidden());
        assertThat(reservas.count()).isEqualTo(antes);
    }

    @Test
    void crearReservaPropiaCalculaPrecioYEstado() throws Exception {
        Map<String, Object> entrada = new HashMap<>(nuevaReserva(cliente.getId()));
        entrada.put("precioTotal", 1);
        entrada.put("estado", "CONFIRMADA");
        http.perform(como(cuerpo(post("/alquilaya/reservas"), entrada), usuarioCliente))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.precioTotal").value(300))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void actualizarReservaAjenaNoAceptaRelacionesFalsificadas() throws Exception {
        Map<String, Object> entrada = Map.of("id", reserva.getId(), "estado", "CANCELADA",
                "cliente", Map.of("id", ajeno.getId()), "propiedad", Map.of("propietario", Map.of("id", duenoAjeno.getId())));
        http.perform(como(cuerpo(put("/alquilaya/reservas"), entrada), otroCliente)).andExpect(status().isForbidden());
        http.perform(como(cuerpo(put("/alquilaya/reservas"), entrada), otroDueno)).andExpect(status().isForbidden());
        assertThat(reserva.getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    void clienteSoloCancelaYDuenoConfirma() throws Exception {
        http.perform(como(cuerpo(put("/alquilaya/reservas"), Map.of("id", reserva.getId(), "estado", "CONFIRMADA")), usuarioCliente))
                .andExpect(status().isForbidden());
        http.perform(como(cuerpo(put("/alquilaya/reservas"), Map.of("id", reserva.getId(), "estado", "CONFIRMADA")), usuarioDueno))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("CONFIRMADA"));
        http.perform(como(cuerpo(put("/alquilaya/reservas"), Map.of("id", reserva.getId(), "estado", "CANCELADA",
                        "cliente", Map.of("id", ajeno.getId()), "precioTotal", 1)), usuarioCliente))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("CANCELADA"))
                .andExpect(jsonPath("$.cliente.id").value(cliente.getId())).andExpect(jsonPath("$.precioTotal").value(200));
    }

    @Test
    void propiedadesAjenasNoSePuedenCrearEditarNiEliminar() throws Exception {
        http.perform(como(cuerpo(post("/alquilaya/propiedades"), Map.of("titulo", "Intento", "propietarioId", dueno.getId())), otroDueno))
                .andExpect(status().isForbidden());
        http.perform(como(cuerpo(put("/alquilaya/propiedades"), Map.of("id", propiedad.getId(), "titulo", "Intento",
                        "propietario", Map.of("id", duenoAjeno.getId()))), otroDueno)).andExpect(status().isForbidden());
        http.perform(como(delete("/alquilaya/propiedades/" + propiedad.getId()), otroDueno)).andExpect(status().isForbidden());
        assertThat(propiedades.findById(propiedad.getId())).isPresent();
        assertThat(propiedad.getTitulo()).isEqualTo("Propiedad ficticia");
    }

    @Test
    void propietarioPuedeCrearEditarYEliminarSuInmueble() throws Exception {
        String salida = http.perform(como(cuerpo(post("/alquilaya/propiedades"),
                        Map.of("titulo", "Otra propiedad", "propietarioId", dueno.getId())), usuarioDueno))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(salida).get("id").asLong();
        http.perform(como(cuerpo(put("/alquilaya/propiedades"), Map.of("id", id, "titulo", "Editada",
                        "propietario", Map.of("id", duenoAjeno.getId()))), usuarioDueno))
                .andExpect(status().isOk()).andExpect(jsonPath("$.titulo").value("Editada"))
                .andExpect(jsonPath("$.propietario.id").value(dueno.getId()));
        http.perform(como(delete("/alquilaya/propiedades/" + id), usuarioDueno)).andExpect(status().isOk());
        assertThat(propiedades.findById(id)).isEmpty();
    }

    @Test
    void sesionAusenteInvalidaODeshabilitadaDevuelve401() throws Exception {
        String ruta = "/alquilaya/reservas/" + reserva.getId();
        http.perform(get(ruta)).andExpect(status().isUnauthorized());
        http.perform(get(ruta).header("Authorization", "Bearer inválido")).andExpect(status().isUnauthorized());
        User inexistente = new User(Long.MAX_VALUE, "no-existe@example.invalid", null, true, List.of());
        http.perform(como(get(ruta), inexistente)).andExpect(status().isUnauthorized());
        String token = jwt.generateToken(new UserSecurity(usuarioCliente));
        usuarioCliente.setEnabled(false);
        usuarios.saveAndFlush(usuarioCliente);
        http.perform(get(ruta).header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized());
    }

    @Test
    void loginIncorrectoDevuelve401() throws Exception {
        http.perform(cuerpo(post("/alquilaya/users/login"), Map.of("username", usuarioCliente.getUsername(),
                        "password", UUID.randomUUID().toString()))).andExpect(status().isUnauthorized());
    }
}
