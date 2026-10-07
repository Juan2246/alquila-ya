package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.VisitaDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.Visita;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.VisitaRepository;
import com.arqui.alquilaya.security.AccesoActual;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.PropiedadService;
import com.arqui.alquilaya.services.VisitaService;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementación del servicio de visitas.
 * Gestiona la programación de visitas de clientes a propiedades.
 * Las visitas inician con estado "PENDIENTE" y pueden cambiar a "COMPLETADA" o "CANCELADA".
 */
@Service
@Transactional
@RequiredArgsConstructor
public class VisitaServiceImpl implements VisitaService {

    private final VisitaRepository visitaRepository;
    private final AccesoActual acceso;

    private final ClienteService clienteService;

    private final PropiedadService propiedadService;

    @Override
    public Visita findById(Long id) {
        Visita visita = visitaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Visita no encontrada"));
        acceso.exigirParticipante(visita); return visita;
    }

    @Override
    public List<Visita> listByClienteId(Long clienteId) {
        acceso.exigirCliente(clienteService.findById(clienteId));
        return visitaRepository.findByCliente_Id(clienteId);
    }

    @Override
    public List<Visita> listByPropiedadId(Long propiedadId) {
        acceso.exigirPropietario(propiedadService.findById(propiedadId).getPropietario());
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

        acceso.exigirCliente(cliente);
        Propiedad propiedad = propiedadService.findById(visitaDTO.getPropiedadId());

        LocalDateTime fecha = (visitaDTO.getFecha() != null && !visitaDTO.getFecha().isBlank())
                ? LocalDateTime.parse(visitaDTO.getFecha())
                : LocalDateTime.now();

        if (!fecha.isAfter(LocalDateTime.now())) throw new ValidationException("Elige una fecha futura");
        String estado = "PENDIENTE";

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
        String estado = visita.getEstado();
        if (estado == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio");
        if (!"CANCELADA".equals(estado)) acceso.exigirPropietario(found.getPropiedad().getPropietario());
        if (!"PENDIENTE".equals(found.getEstado()) || !List.of("CANCELADA", "COMPLETADA").contains(estado))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transición de visita no válida");
        if ("COMPLETADA".equals(estado) && found.getFecha().isAfter(LocalDateTime.now()))
            throw new ValidationException("La visita todavía no ha ocurrido");
        found.setEstado(estado);
        return visitaRepository.save(found);
    }
}
