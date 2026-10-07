package com.arqui.alquilaya.dtos;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir datos de reservas entre controller y service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDTO {
    private Long id;
    private Long contratoId;
    @NotNull
    private String fechaCheckIn;
    @NotNull
    private String fechaCheckOut;
    private String estado;
    private BigDecimal precioTotal;
    @NotNull
    private Long clienteId;
    @NotNull
    private Long propiedadId;
    private String clienteNombre;
    private String propiedadTitulo;
}