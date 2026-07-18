package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoritoDTO {
    private Long id;
    private Long clienteId;
    private Long propiedadId;
    private String propiedadTitulo;
}
