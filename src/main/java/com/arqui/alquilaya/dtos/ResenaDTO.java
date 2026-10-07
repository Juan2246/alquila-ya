package com.arqui.alquilaya.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaDTO {
    private Long id;
    private Integer puntuacion;  // Se calcula automáticamente como promedio
    @NotNull
    private Integer puntuacionLimpieza;
    @NotNull
    private Integer puntuacionUbicacion;
    @NotNull
    private Integer puntuacionComunicacion;
    private String comentario;
    @NotNull
    private Long clienteId;
    @NotNull
    private Long propiedadId;
    private String clienteNombre;
    private String propiedadTitulo;
}