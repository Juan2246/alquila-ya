package com.arqui.alquilaya;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import static org.assertj.core.api.Assertions.assertThat;

/** Servidor Tomcat real, HTTP real, JWT real y H2. No mocks de servicios/repositorios. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegracionApiTests.TiempoDePrueba.class)
class IntegracionApiTests extends EntornoDePrueba {
    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    @Autowired @Qualifier("relojPrueba") RelojMutable reloj;
    private final HttpClient http = HttpClient.newHttpClient();
    private record Cuenta(long perfil, String token) {}

    static class RelojMutable extends Clock {
        private Instant ahora = Instant.now();
        public ZoneId getZone() { return ZoneId.systemDefault(); }
        public Clock withZone(ZoneId zone) { return Clock.fixed(ahora, zone); }
        public Instant instant() { return ahora; }
        void avanzar(Duration duracion) { ahora = ahora.plus(duracion); }
    }
    @TestConfiguration
    static class TiempoDePrueba {
        @Bean @Primary RelojMutable relojPrueba() { return new RelojMutable(); }
    }

    private HttpResponse<byte[]> enviar(String metodo, String ruta, String token, byte[] datos, String tipo) throws Exception {
        var req = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .timeout(Duration.ofSeconds(15)).method(metodo, datos == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofByteArray(datos));
        if (token != null) req.header("Authorization", "Bearer " + token);
        if (tipo != null) req.header("Content-Type", tipo);
        return http.send(req.build(), HttpResponse.BodyHandlers.ofByteArray());
    }
    private JsonNode api(String metodo, String ruta, Cuenta cuenta, Object entrada, int esperado) throws Exception {
        var respuesta = enviar(metodo, "/alquilaya" + ruta, cuenta == null ? null : cuenta.token(),
                entrada == null ? null : json.writeValueAsBytes(entrada), "application/json");
        assertThat(respuesta.statusCode()).as(metodo + " " + ruta).isEqualTo(esperado);
        return respuesta.body().length == 0 ? json.nullNode() : json.readTree(respuesta.body());
    }
    private Cuenta registrar(String rol) throws Exception {
        String nombre = "prueba-" + UUID.randomUUID(), clave = UUID.randomUUID().toString();
        var perfil = api("POST", "/users/registro-completo", null, Map.of("username", nombre, "password", clave,
                "rol", rol, "nombre", "Persona", "apellido", "Ficticia", "dni", "12345678", "correo", nombre + "@example.invalid", "edad", 25), 201);
        assertThat(perfil.get("perfilId").asLong()).isPositive();
        var token = api("POST", "/users/login", null, Map.of("username", nombre, "password", clave), 200);
        return new Cuenta(perfil.get("perfilId").asLong(), token.get("jwtToken").asText());
    }
    private byte[] imagen() throws Exception {
        var salida = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB), "png", salida);
        return salida.toByteArray();
    }
    private HttpResponse<byte[]> archivo(String ruta, Cuenta cuenta, byte[] contenido, String nombre) throws Exception {
        String borde = "limite-" + UUID.randomUUID();
        var cuerpo = new ByteArrayOutputStream();
        cuerpo.write(("--" + borde + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"" + nombre
                + "\"\r\nContent-Type: image/png\r\n\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        cuerpo.write(contenido); cuerpo.write(("\r\n--" + borde + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return enviar("POST", "/alquilaya" + ruta, cuenta.token(), cuerpo.toByteArray(), "multipart/form-data; boundary=" + borde);
    }

    @Test
    void recorridoCompletoYAccesosAjenosConHttpReal() throws Exception {
        Cuenta dueno = registrar("ROLE_PROPIETARIO"), otroDueno = registrar("ROLE_PROPIETARIO");
        Cuenta cliente = registrar("ROLE_CLIENTE"), ajeno = registrar("ROLE_CLIENTE");
        var catalogo = api("GET", "/comodidades", dueno, null, 200);
        assertThat(catalogo.size()).isGreaterThanOrEqualTo(4);
        long comodidad = catalogo.get(0).get("id").asLong();
        var inmueble = api("POST", "/propiedades", dueno, Map.of("titulo", "Casa de integración", "descripcion", "Datos ficticios",
                "ubicacion", "Dirección de prueba", "distrito", "Lima", "precio", 100, "habitaciones", 2,
                "capacidad", 3, "propietarioId", dueno.perfil(), "comodidadIds", List.of(comodidad)), 201);
        long propiedad = inmueble.get("id").asLong();
        var subida = archivo("/archivos/propiedad", dueno, imagen(), "foto.svg"); // se determina el tipo real, no la extensión
        assertThat(subida.statusCode()).isEqualTo(201);
        String urlFoto = json.readTree(subida.body()).get("url").asText();
        assertThat(urlFoto).endsWith(".png");
        api("POST", "/propiedades/" + propiedad + "/fotos", otroDueno, Map.of("url", urlFoto), 403);
        api("POST", "/propiedades/" + propiedad + "/fotos", dueno, Map.of("url", urlFoto), 201);
        assertThat(enviar("GET", urlFoto, null, null, null).statusCode()).isEqualTo(200);
        assertThat(archivo("/archivos/propiedad", dueno, "<svg onload='alert(1)'/>".getBytes(), "imagen.png").statusCode()).isEqualTo(406);
        var regla = api("POST", "/propiedades/" + propiedad + "/clausulas", dueno, Map.of("texto", "Sin humo"), 201);
        api("DELETE", "/propiedades/" + propiedad + "/clausulas/" + regla.get("id").asLong(), otroDueno, null, 403);
        var detalle = api("GET", "/propiedades/" + propiedad, cliente, null, 200);
        assertThat(detalle.get("fotos").size()).isEqualTo(1); assertThat(detalle.get("clausulas").size()).isEqualTo(1);
        assertThat(detalle.toString()).doesNotContain("\"dni\"", "\"correo\"", "\"user\"", "\"password\"");
        api("DELETE", "/propiedades/" + propiedad + "/clausulas/" + regla.get("id").asLong(), dueno, null, 204);
        api("PUT", "/propiedades", dueno, Map.of("id", propiedad, "comodidadIds", List.of()), 200);
        assertThat(api("GET", "/propiedades/" + propiedad, cliente, null, 200).get("comodidades").size()).isZero();
        assertThat(api("GET", "/propiedades/buscar?distrito=Lima&precioMax=150", cliente, null, 200).size()).isPositive();
        LocalDate entrada = LocalDate.now(reloj).plusDays(1), salida = entrada.plusDays(2);
        var cotizacion = api("GET", "/propiedades/" + propiedad + "/cotizar?checkIn=" + entrada + "&checkOut=" + salida, cliente, null, 200);
        assertThat(cotizacion.get("precioTotal").asInt()).isEqualTo(200);
        Map<String, Object> solicitud = Map.of("clienteId", cliente.perfil(), "propiedadId", propiedad,
                "fechaCheckIn", entrada.toString(), "fechaCheckOut", salida.toString(), "precioTotal", 1);
        api("POST", "/reservas", ajeno, solicitud, 403);
        var reserva = api("POST", "/reservas", cliente, solicitud, 201);
        long reservaId = reserva.get("id").asLong(), contrato = reserva.get("contratoId").asLong();
        assertThat(contrato).isPositive(); assertThat(reserva.get("precioTotal").asInt()).isEqualTo(200);
        api("POST", "/reservas", cliente, solicitud, 406); // no segunda reserva ni contrato huérfano
        api("GET", "/contratos/" + contrato, ajeno, null, 403);
        api("GET", "/contratos/" + contrato, otroDueno, null, 403);
        api("GET", "/contratos/cliente/" + cliente.perfil(), ajeno, null, 403);
        api("GET", "/contratos/propiedad/" + propiedad, otroDueno, null, 403);
        assertThat(api("GET", "/contratos/propiedad/" + propiedad, dueno, null, 200).size()).isEqualTo(1);
        assertThat(api("GET", "/contratos", ajeno, null, 200).size()).isZero();
        api("POST", "/contratos", cliente, Map.of("clienteId", cliente.perfil(), "propiedadId", propiedad), 409);
        assertThat(archivo("/contratos/" + contrato + "/firmar", cliente, imagen(), "firma.png").statusCode()).isEqualTo(409);
        api("PUT", "/reservas", dueno, Map.of("id", reservaId, "estado", "CONFIRMADA"), 200);
        assertThat(archivo("/contratos/" + contrato + "/firmar", dueno, imagen(), "firma.png").statusCode()).isEqualTo(403);
        assertThat(archivo("/contratos/" + contrato + "/firmar", ajeno, imagen(), "firma.png").statusCode()).isEqualTo(403);
        assertThat(archivo("/contratos/" + contrato + "/firmar", cliente, imagen(), "firma.png").statusCode()).isEqualTo(200);
        assertThat(archivo("/contratos/" + contrato + "/firmar", cliente, imagen(), "firma.png").statusCode()).isEqualTo(409);
        assertThat(enviar("GET", "/alquilaya/contratos/" + contrato + "/firma", cliente.token(), null, null).statusCode()).isEqualTo(200);
        assertThat(enviar("GET", "/alquilaya/contratos/" + contrato + "/firma", dueno.token(), null, null).statusCode()).isEqualTo(200);
        api("GET", "/contratos/" + contrato + "/firma", ajeno, null, 403);
        api("GET", "/contratos/" + contrato + "/firma", null, null, 401);
        assertThat(enviar("GET", "/uploads/firmas/archivo.png", dueno.token(), null, null).statusCode()).isEqualTo(403);
        api("GET", "/pagos/contrato/" + contrato, ajeno, null, 403);
        var pago = Map.of("contratoId", contrato, "monto", 200, "metodo", "Transferencia");
        api("POST", "/pagos", cliente, pago, 403); api("POST", "/pagos", otroDueno, pago, 403);
        api("POST", "/pagos", dueno, pago, 201); api("POST", "/pagos", dueno, pago, 406);
        assertThat(api("GET", "/pagos/contrato/" + contrato, cliente, null, 200).size()).isEqualTo(1);
        var avisos = api("GET", "/notificaciones/propietario/" + dueno.perfil(), dueno, null, 200);
        assertThat(avisos.size()).isPositive(); long aviso = avisos.get(0).get("id").asLong();
        api("GET", "/notificaciones/propietario/" + dueno.perfil(), otroDueno, null, 403);
        api("PUT", "/notificaciones/" + aviso + "/leer", ajeno, Map.of(), 403);
        api("PUT", "/notificaciones/" + aviso + "/leer", dueno, Map.of(), 200);
        api("GET", "/notificaciones/cliente/" + cliente.perfil(), ajeno, null, 403);
        var visita = Map.of("clienteId", cliente.perfil(), "propiedadId", propiedad, "fecha", LocalDateTime.now().plusDays(1).toString());
        api("POST", "/visitas", ajeno, visita, 403);
        long visitaId = api("POST", "/visitas", cliente, visita, 201).get("id").asLong();
        api("GET", "/visitas/cliente/" + cliente.perfil(), ajeno, null, 403);
        api("GET", "/visitas/propiedad/" + propiedad, otroDueno, null, 403);
        api("PUT", "/visitas", ajeno, Map.of("id", visitaId, "estado", "CANCELADA"), 403);
        api("PUT", "/visitas", cliente, Map.of("id", visitaId, "estado", "COMPLETADA"), 403);
        api("PUT", "/visitas", cliente, Map.of("id", visitaId, "estado", "CANCELADA"), 200);
        api("PUT", "/visitas", dueno, Map.of("id", visitaId, "estado", "COMPLETADA"), 409);
        var resena = Map.of("clienteId", cliente.perfil(), "propiedadId", propiedad, "puntuacionLimpieza", 5,
                "puntuacionUbicacion", 4, "puntuacionComunicacion", 5, "comentario", "Estancia de prueba");
        api("POST", "/resenas", ajeno, resena, 403);
        api("POST", "/resenas", cliente, resena, 404);
        api("PUT", "/reservas", dueno, Map.of("id", reservaId, "estado", "COMPLETADA"), 409);
        reloj.avanzar(Duration.ofDays(4)); // avanza solo el reloj inyectado de reservas, no los datos ni el reloj del sistema
        api("PUT", "/reservas", dueno, Map.of("id", reservaId, "estado", "COMPLETADA"), 200);
        api("PUT", "/reservas", cliente, Map.of("id", reservaId, "estado", "CANCELADA"), 409);
        var opinion = api("POST", "/resenas", cliente, resena, 201);
        assertThat(opinion.get("cliente").get("nombre").asText()).isEqualTo("Persona");
        assertThat(opinion.has("fecha")).isTrue(); long resenaId = opinion.get("id").asLong();
        api("PUT", "/resenas/" + resenaId + "/responder", otroDueno, Map.of("respuesta", "Intento"), 403);
        api("PUT", "/resenas/" + resenaId + "/responder", cliente, Map.of("respuesta", "Intento"), 403);
        api("PUT", "/resenas/" + resenaId + "/responder", dueno, Map.of("respuesta", "Gracias por tu visita"), 200);
        assertThat(api("GET", "/resenas/propiedad/" + propiedad, cliente, null, 200).get(0).get("respuestaPropietario").asText()).isEqualTo("Gracias por tu visita");
        var segunda = api("POST", "/reservas", cliente, Map.of("clienteId", cliente.perfil(), "propiedadId", propiedad,
                "fechaCheckIn", LocalDate.now(reloj).plusDays(1).toString(), "fechaCheckOut", LocalDate.now(reloj).plusDays(2).toString()), 201);
        api("PUT", "/reservas", cliente, Map.of("id", segunda.get("id").asLong(), "estado", "CANCELADA"), 200);
        assertThat(api("GET", "/contratos/" + segunda.get("contratoId").asLong(), cliente, null, 200).get("estado").asText()).isEqualTo("CANCELADO");
    }

    @Test
    void reservasConcurrentesNoDuplicanLaEstanciaNiElContrato() throws Exception {
        Cuenta dueno = registrar("ROLE_PROPIETARIO"), cliente = registrar("ROLE_CLIENTE");
        long propiedad = api("POST", "/propiedades", dueno, Map.of("titulo", "Prueba concurrente", "precio", 100,
                "propietarioId", dueno.perfil()), 201).get("id").asLong();
        byte[] entrada = json.writeValueAsBytes(Map.of("clienteId", cliente.perfil(), "propiedadId", propiedad,
                "fechaCheckIn", LocalDate.now(reloj).plusDays(10).toString(), "fechaCheckOut", LocalDate.now(reloj).plusDays(12).toString()));
        var inicio = new java.util.concurrent.CountDownLatch(1);
        try (var pool = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<Integer> reservar = () -> { inicio.await(); return enviar("POST", "/alquilaya/reservas", cliente.token(), entrada, "application/json").statusCode(); };
            var a = pool.submit(reservar); var b = pool.submit(reservar); inicio.countDown();
            assertThat(List.of(a.get(), b.get())).containsExactlyInAnyOrder(201, 406);
        }
        assertThat(api("GET", "/reservas/propiedad/" + propiedad, dueno, null, 200).size()).isEqualTo(1);
        assertThat(api("GET", "/contratos/propiedad/" + propiedad, dueno, null, 200).size()).isEqualTo(1);
    }

    @Test
    void favoritosSoloPermitenAccesoDelClienteTitular() throws Exception {
        Cuenta dueno = registrar("ROLE_PROPIETARIO"), cliente = registrar("ROLE_CLIENTE"), ajeno = registrar("ROLE_CLIENTE");
        long propiedad = api("POST", "/propiedades", dueno, Map.of("titulo", "Favorito de prueba", "precio", 100,
                "propietarioId", dueno.perfil()), 201).get("id").asLong();
        var entrada = Map.of("clienteId", cliente.perfil(), "propiedadId", propiedad);
        api("POST", "/favoritos", ajeno, entrada, 403);
        long favorito = api("POST", "/favoritos", cliente, entrada, 201).get("id").asLong();
        api("GET", "/favoritos/cliente/" + cliente.perfil(), ajeno, null, 403);
        assertThat(api("GET", "/favoritos/cliente/" + cliente.perfil(), cliente, null, 200).size()).isEqualTo(1);
        api("DELETE", "/favoritos/" + favorito, ajeno, null, 403);
        api("DELETE", "/favoritos/" + favorito, cliente, null, 200);
        assertThat(api("GET", "/favoritos/cliente/" + cliente.perfil(), cliente, null, 200).size()).isZero();
    }

    @Test
    void registroInvalidoYCorsLimitado() throws Exception {
        api("POST", "/users/registro-completo", null, Map.of("username", "incompleto", "password", "entrada-invalida", "rol", "ROLE_ADMIN"), 400);
        var req = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/alquilaya/users/login"))
                .header("Origin", "https://no-permitido.invalid").header("Access-Control-Request-Method", "POST")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        assertThat(http.send(req, HttpResponse.BodyHandlers.discarding()).statusCode()).isEqualTo(403);
        var local = HttpRequest.newBuilder(req.uri()).header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST").method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        assertThat(http.send(local, HttpResponse.BodyHandlers.discarding()).headers().firstValue("Access-Control-Allow-Origin")).contains("http://localhost:4200");
    }
}
