package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para devolver el resultado de una cotización automática.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotizacionDTO {
    private Long propiedadId;
    private String propiedadTitulo;
    private String fechaCheckIn;
    private String fechaCheckOut;
    private Long noches;
    private BigDecimal precioPorNoche;
    private BigDecimal precioTotal;
}
