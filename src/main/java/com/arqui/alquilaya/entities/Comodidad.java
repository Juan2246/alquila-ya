package com.arqui.alquilaya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad que representa una comodidad/servicio que puede ofrecer una propiedad.
 * Ejemplos: Wifi, TV, Cocina, Piscina, Estacionamiento, etc.
 * Relación ManyToMany con Propiedad.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "comodidades")
public class Comodidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la comodidad (ej: "Wifi", "Piscina", "Cocina")
    private String nombre;

    // Nombre del ícono para el frontend (ej: "wifi", "tv", "kitchen")
    private String icono;

    @JsonIgnore
    @ManyToMany(mappedBy = "comodidades")
    private List<Propiedad> propiedades;
}
