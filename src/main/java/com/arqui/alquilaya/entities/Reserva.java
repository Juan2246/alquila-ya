package com.arqui.alquilaya.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una reserva de una propiedad.
 * Es el paso intermedio antes del contrato: el cliente separa fechas (check-in/check-out)
 * para que queden bloqueadas en el calendario de la propiedad.
 * Estados: PENDIENTE, CONFIRMADA, COMPLETADA, CANCELADA
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha de check-in (inicio de la estancia)
    private LocalDate fechaCheckIn;

    // Fecha de check-out (fin de la estancia)
    private LocalDate fechaCheckOut;

    // Estado de la reserva: PENDIENTE, CONFIRMADA, COMPLETADA, CANCELADA
    private String estado;

    // Precio total calculado automáticamente (noches × precio base)
    @Column(precision = 10, scale = 2)
    private BigDecimal precioTotal;

    // Fecha en que se creó la reserva
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;
}
