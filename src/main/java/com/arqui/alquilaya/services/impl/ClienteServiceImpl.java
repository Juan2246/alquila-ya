package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.repositories.ClienteRepository;
import com.arqui.alquilaya.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de clientes.
 * Gestiona las operaciones de perfil de los inquilinos/buscadores de alquiler.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public Cliente add(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Override
    public List<Cliente> listAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente findByUserId(Long userId) {
        return clienteRepository.findByUser_Id(userId);
    }
}
