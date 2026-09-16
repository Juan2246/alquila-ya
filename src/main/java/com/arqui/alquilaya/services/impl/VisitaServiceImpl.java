package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.VisitaDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.Visita;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.VisitaRepository;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.PropiedadService;
import com.arqui.alquilaya.services.VisitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de visitas.
 * Gestiona la programación de visitas de clientes a propiedades.
 * Las visitas inician con estado "PENDIENTE" y pueden cambiar a "COMPLETADA" o "CANCELADA".
 */
@Service
public class VisitaServiceImpl implements VisitaService {

    @Autowired
    VisitaRepository visitaRepository;

    @Autowired
    ClienteService clienteService;

    @Autowired
    PropiedadService propiedadService;

    @Override
    public Visita findById(Long id) {
        return visitaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Visita> listByClienteId(Long clienteId) {
        return visitaRepository.findByCliente_Id(clienteId);
    }

    @Override
    public List<Visita> listByPropiedadId(Long propiedadId) {
        return visitaRepository.findByPropiedad_Id(propiedadId);
    }

    /**
     * Crea una nueva visita a partir de un DTO.
     * 1. Busca al cliente y la propiedad por sus IDs.
     * 2. Establece el estado inicial como "PENDIENTE".
     * 3. Asigna la fecha de visita (si no se proporciona, usa la fecha actual).
     */
    @Override
    public VisitaDTO addDTO(VisitaDTO visitaDTO) {
        Cliente cliente = clienteService.findById(visitaDTO.getClienteId());
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id: " + visitaDTO.getClienteId() + " no encontrado");
        }

        Propiedad propiedad = propiedadService.findById(visitaDTO.getPropiedadId());

        LocalDateTime fecha = (visitaDTO.getFecha() != null && !visitaDTO.getFecha().isBlank())
                ? LocalDateTime.parse(visitaDTO.getFecha())
                : LocalDateTime.now();

        String estado = (visitaDTO.getEstado() != null && !visitaDTO.getEstado().isBlank())
                ? visitaDTO.getEstado()
                : "PENDIENTE";

        Visita newVisita = new Visita(null, fecha, estado, cliente, propiedad);
        newVisita = visitaRepository.save(newVisita);

        visitaDTO.setId(newVisita.getId());
        visitaDTO.setEstado(newVisita.getEstado());
        visitaDTO.setClienteNombre(cliente.getNombre() + " " + cliente.getApellido());
        visitaDTO.setPropiedadTitulo(propiedad.getTitulo());
        return visitaDTO;
    }

    /**
     * Actualiza el estado de una visita existente (ej: de PENDIENTE a COMPLETADA).
     */
    @Override
    public Visita update(Visita visita) {
        Visita found = findById(visita.getId());
        if (found == null) {
            throw new ResourceNotFoundException("Visita con id: " + visita.getId() + " no encontrada");
        }
        if (visita.getEstado() != null) {
            found.setEstado(visita.getEstado());
        }
        if (visita.getFecha() != null) {
            found.setFecha(visita.getFecha());
        }
        return visitaRepository.save(found);
    }
}
