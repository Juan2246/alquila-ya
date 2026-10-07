package com.arqui.alquilaya.dtos;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {
    private Long id;
    @NotNull
    private BigDecimal monto;   // BigDecimal para precisión monetaria
    private String fecha;
    private String estado;
    @NotNull
    private String metodo;
    @NotNull
    private Long contratoId;
}