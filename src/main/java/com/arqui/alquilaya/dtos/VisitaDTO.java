package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitaDTO {
    private Long id;
    private String fecha;
    private String estado;
    private Long clienteId;
    private Long propiedadId;
    private String clienteNombre;
    private String propiedadTitulo;
}
