package com.arqui.alquilaya.services;

import com.arqui.alquilaya.services.impl.RolServiceImpl;
import com.arqui.alquilaya.entities.Rol;

/**
 * Interfaz de servicio para la gestión de roles del sistema.
 * Define el contrato (métodos disponibles) sin implementar la lógica.
 * La implementación concreta está en RolServiceImpl.
 */
public interface RolService {
    public Rol findById(Long id);
    public Rol findByNombre(String nombre);
    public Rol add(Rol rol);
}
