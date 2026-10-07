package com.arqui.alquilaya.dtos;

import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para devolver la información completa del perfil del usuario autenticado.
 * Unifica datos de User + Cliente/Propietario según el rol.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfilDTO {
    private Long userId;
    private String username;
    private String rol;

    // Datos del perfil (Cliente o Propietario)
    private Long perfilId;
    private String nombre;
    private String apellido;
    private String dni;
    private String correo;
    private Integer edad;
    private String foto;
    private String descripcion; // descripcion para Cliente, observaciones para Propietario
}
