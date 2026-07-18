package com.arqui.alquilaya.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tickets_soporte")
public class TicketSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String motivo;
    private String descripcion;

    // Estado del ticket: ABIERTO, EN_PROCESO, RESUELTO, CERRADO
    private String estado;

    // Prioridad: BAJA, MEDIA, ALTA, URGENTE
    private String prioridad;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    @ManyToOne
    @JoinColumn(name = "soporte_id")
    private Soporte soporte;
}
