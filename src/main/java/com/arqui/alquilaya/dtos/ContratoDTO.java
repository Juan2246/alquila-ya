package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratoDTO {
    private Long id;
    private String fechaInicio;
    private String fechaFin;
    private String pdf;
    private String estado;
    private String firmaImagenUrl;
    private Long clienteId;
    private Long propiedadId;
    private String clienteNombre;
    private String propiedadTitulo;
}
