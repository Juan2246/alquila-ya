package com.arqui.alquilaya.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitaDTO {
    private Long id;
    @NotNull
    private String fecha;
    private String estado;
    @NotNull
    private Long clienteId;
    @NotNull
    private Long propiedadId;
    private String clienteNombre;
    private String propiedadTitulo;
}