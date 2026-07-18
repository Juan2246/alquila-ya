package com.arqui.alquilaya.services;

import com.arqui.alquilaya.entities.Propietario;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de perfiles de propietarios.
 */
public interface PropietarioService {
    public Propietario add(Propietario propietario);
    public Propietario findById(Long id);
    public List<Propietario> listAll();
    public Propietario findByUserId(Long userId);
}
