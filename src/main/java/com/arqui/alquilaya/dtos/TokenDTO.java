package com.arqui.alquilaya.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    private String jwtToken;   // El token JWT que el cliente usará en el header Authorization
    private Long id;           // ID del usuario autenticado
    private String roles;      // Roles del usuario separados por ";"
}
