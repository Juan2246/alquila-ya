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
@Table(name = "visitas")
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha y hora programada para la visita al inmueble
    private LocalDateTime fecha;

    // Estado de la visita: PENDIENTE, COMPLETADA, CANCELADA
    private String estado;

    /**
     * Relación muchos-a-uno con Cliente: un cliente puede tener muchas visitas.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /**
     * Relación muchos-a-uno con Propiedad: una propiedad puede recibir muchas visitas.
     */
    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;
}
