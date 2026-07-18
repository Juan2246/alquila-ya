package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para transferir datos de reservas entre controller y service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDTO {
    private Long id;
    private String fechaCheckIn;
    private String fechaCheckOut;
    private String estado;
    private BigDecimal precioTotal;
    private Long clienteId;
    private Long propiedadId;
    private String clienteNombre;
    private String propiedadTitulo;
}
