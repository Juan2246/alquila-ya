package com.arqui.alquilaya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "propietarios")
public class Propietario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    @JsonIgnore
    private String dni;
    @JsonIgnore
    private String correo;
    @JsonIgnore
    private Integer edad;
    private String foto;
    private String observaciones;

    /**
     * Relación uno-a-uno con User: cada propietario tiene exactamente un usuario de autenticación.
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    /**
     * Relación uno-a-muchos con Propiedad: un propietario puede tener muchas propiedades.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "propietario", fetch = FetchType.EAGER)
    private List<Propiedad> propiedades;
}
