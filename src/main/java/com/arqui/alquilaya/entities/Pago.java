package com.arqui.alquilaya.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    // Fecha en que se realizó el pago
    private LocalDateTime fecha;

    // Estado del pago: PENDIENTE, COMPLETADO, RECHAZADO
    private String estado;

    private String metodo;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;
}
