package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {
    private Long id;
    private BigDecimal monto;   // BigDecimal para precisión monetaria
    private String fecha;
    private String estado;
    private String metodo;
    private Long contratoId;
}
