package com.arqui.alquilaya.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "propiedad_fotos")
public class PropiedadFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // URL o ruta de la imagen del inmueble
    private String url;


    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;
}
