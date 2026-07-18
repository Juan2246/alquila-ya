package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaDTO {
    private Long id;
    private Integer puntuacion;  // Se calcula automáticamente como promedio
    private Integer puntuacionLimpieza;
    private Integer puntuacionUbicacion;
    private Integer puntuacionComunicacion;
    private String comentario;
    private Long clienteId;
    private Long propiedadId;
    private String clienteNombre;
    private String propiedadTitulo;
}
