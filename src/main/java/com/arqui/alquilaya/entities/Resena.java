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
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Puntuación general (promedio automático de las 3 subcategorías)
    private Integer puntuacion;

    // Subcategorías de calificación (1-5 cada una)
    private Integer puntuacionLimpieza;
    private Integer puntuacionUbicacion;
    private Integer puntuacionComunicacion;

    // Texto libre con los comentarios del cliente sobre la propiedad
    private String comentario;

    // Fecha en que se publicó la reseña
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
