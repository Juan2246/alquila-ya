package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropiedadDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String distrito;
    private BigDecimal precio;
    private Integer habitaciones;
    private Integer capacidad;
    private Double latitud;
    private Double longitud;
    private Long propietarioId;
    // Campo derivado: nombre completo del propietario para mostrar en la respuesta
    private String propietarioNombre;
    // IDs de comodidades para asignar al crear/editar
    private List<Long> comodidadIds;
}
