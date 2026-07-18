package com.arqui.alquilaya.services;

import com.arqui.alquilaya.entities.Cliente;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de perfiles de clientes.
 */
public interface ClienteService {
    public Cliente add(Cliente cliente);
    public Cliente findById(Long id);
    public List<Cliente> listAll();
    public Cliente findByUserId(Long userId);
}
