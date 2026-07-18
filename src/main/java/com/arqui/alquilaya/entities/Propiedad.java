package com.arqui.alquilaya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "propiedades")
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String distrito;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    private Integer habitaciones;

    // Número máximo de huéspedes que admite la propiedad
    private Integer capacidad;

    // Coordenadas geográficas para ubicación en mapa
    private Double latitud;
    private Double longitud;

    // Fecha de publicación del inmueble
    private LocalDateTime fechaPublicacion;

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    // Relación ManyToMany con Comodidad
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "propiedad_comodidades",
            joinColumns = @JoinColumn(name = "propiedad_id"),
            inverseJoinColumns = @JoinColumn(name = "comodidad_id")
    )
    private List<Comodidad> comodidades;

    // Relaciones inversas hacia las sub-entidades de la propiedad
    @JsonIgnore
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private List<PropiedadFoto> fotos;

    @JsonIgnore
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private List<PropiedadClausula> clausulas;

    @JsonIgnore
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private List<Visita> visitas;

    @JsonIgnore
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private List<Favorito> favoritos;

    @JsonIgnore
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private List<Resena> resenas;

    @JsonIgnore
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private List<Contrato> contratos;

    @JsonIgnore
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private List<Reserva> reservas;
}
