package com.arqui.alquilaya.dtos;

import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Comodidad;
import com.arqui.alquilaya.entities.Contrato;
import com.arqui.alquilaya.entities.Notificacion;
import com.arqui.alquilaya.entities.Pago;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.PropiedadClausula;
import com.arqui.alquilaya.entities.PropiedadFoto;
import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.entities.Resena;
import com.arqui.alquilaya.entities.Reserva;
import com.arqui.alquilaya.entities.Visita;
import com.arqui.alquilaya.entities.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

/** Contratos de lectura explícitos: no serializan cuentas, DNI ni correos. */
public final class LecturasDTO {
    private LecturasDTO() {}
    public record Persona(Long id, String nombre, String apellido, String foto) {}
    public record ComodidadLectura(Long id, String nombre, String icono) {}
    public record Foto(Long id, String url) {}
    public record Clausula(Long id, String texto) {}
    public record PropiedadLectura(Long id, String titulo, String descripcion, String ubicacion, String distrito,
            BigDecimal precio, Integer habitaciones, Integer capacidad, Double latitud, Double longitud,
            LocalDateTime fechaPublicacion, Persona propietario, List<ComodidadLectura> comodidades,
            List<Foto> fotos, List<Clausula> clausulas) {}
    public record ReservaLectura(Long id, String contratoEstado, LocalDate fechaCheckIn, LocalDate fechaCheckOut, String estado,
            BigDecimal precioTotal, LocalDateTime fechaCreacion, Persona cliente, PropiedadLectura propiedad) {}
    public record ContratoLectura(Long id, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado,
            String firmaImagenUrl, Persona cliente, PropiedadLectura propiedad, Long reservaId) {}
    public record VisitaLectura(Long id, LocalDateTime fecha, String estado, Persona cliente, PropiedadLectura propiedad) {}
    public record ReferenciaPropiedad(Long id, String titulo) {}
    public record ResenaLectura(Long id, ReferenciaPropiedad propiedad, Integer puntuacion, Integer puntuacionLimpieza, Integer puntuacionUbicacion,
            Integer puntuacionComunicacion, String comentario, LocalDateTime fecha, Persona cliente, String respuestaPropietario) {}
    public record PagoLectura(Long id, BigDecimal monto, LocalDateTime fecha, String estado, String metodo, Long contratoId) {}
    public record NotificacionLectura(Long id, String titulo, String mensaje, LocalDateTime fecha, Boolean leida) {}

    public static Persona persona(Cliente c) { return c == null ? null : new Persona(c.getId(), c.getNombre(), c.getApellido(), c.getFoto()); }
    public static Persona persona(Propietario p) { return p == null ? null : new Persona(p.getId(), p.getNombre(), p.getApellido(), p.getFoto()); }
    public static ComodidadLectura comodidad(Comodidad c) { return new ComodidadLectura(c.getId(), c.getNombre(), c.getIcono()); }
    public static Foto foto(PropiedadFoto f) { return new Foto(f.getId(), f.getUrl()); }
    public static Clausula clausula(PropiedadClausula c) { return new Clausula(c.getId(), c.getTexto()); }
    public static PropiedadLectura propiedad(Propiedad p) {
        return new PropiedadLectura(p.getId(), p.getTitulo(), p.getDescripcion(), p.getUbicacion(), p.getDistrito(),
                p.getPrecio(), p.getHabitaciones(), p.getCapacidad(), p.getLatitud(), p.getLongitud(), p.getFechaPublicacion(), persona(p.getPropietario()),
                p.getComodidades() == null ? List.of() : p.getComodidades().stream().map(LecturasDTO::comodidad).toList(),
                p.getFotos() == null ? List.of() : p.getFotos().stream().map(LecturasDTO::foto).toList(),
                p.getClausulas() == null ? List.of() : p.getClausulas().stream().map(LecturasDTO::clausula).toList());
    }
    public static ReservaLectura reserva(Reserva r) { return new ReservaLectura(r.getId(), r.getContrato() == null ? null : r.getContrato().getEstado(), r.getFechaCheckIn(), r.getFechaCheckOut(), r.getEstado(), r.getPrecioTotal(), r.getFechaCreacion(), persona(r.getCliente()), propiedad(r.getPropiedad())); }
    public static ContratoLectura contrato(Contrato c) { return new ContratoLectura(c.getId(), c.getFechaInicio(), c.getFechaFin(), c.getEstado(), c.getFirmaImagenUrl() == null ? null : "/alquilaya/contratos/" + c.getId() + "/firma", persona(c.getCliente()), propiedad(c.getPropiedad()), c.getReserva() == null ? null : c.getReserva().getId()); }
    public static VisitaLectura visita(Visita v) { return new VisitaLectura(v.getId(), v.getFecha(), v.getEstado(), persona(v.getCliente()), propiedad(v.getPropiedad())); }
    public static ResenaLectura resena(Resena r) { return new ResenaLectura(r.getId(), new ReferenciaPropiedad(r.getPropiedad().getId(), r.getPropiedad().getTitulo()), r.getPuntuacion(), r.getPuntuacionLimpieza(), r.getPuntuacionUbicacion(), r.getPuntuacionComunicacion(), r.getComentario(), r.getFecha(), persona(r.getCliente()), r.getRespuestaPropietario()); }
    public static PagoLectura pago(Pago p) { return new PagoLectura(p.getId(), p.getMonto(), p.getFecha(), p.getEstado(), p.getMetodo(), p.getContrato().getId()); }
    public static NotificacionLectura notificacion(Notificacion n) { return new NotificacionLectura(n.getId(), n.getTitulo(), n.getMensaje(), n.getFecha(), n.getLeida()); }
}
