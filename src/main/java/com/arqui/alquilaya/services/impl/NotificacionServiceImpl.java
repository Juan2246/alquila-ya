package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.entities.Notificacion;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.NotificacionRepository;
import com.arqui.alquilaya.security.AccesoActual;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.NotificacionService;
import com.arqui.alquilaya.services.PropietarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de notificaciones.
 * Gestiona el envío y marcado de notificaciones como leídas para los clientes.
 */
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final AccesoActual acceso;
    private final ClienteService clientes;
    private final PropietarioService propietarios;

    @Override
    public List<Notificacion> listByPropietarioId(Long id) {
        acceso.exigirPropietario(propietarios.findById(id));
        return notificacionRepository.findByPropietario_Id(id);
    }

    @Override
    public List<Notificacion> listByClienteId(Long clienteId) {
        acceso.exigirCliente(clientes.findById(clienteId));
        return notificacionRepository.findByCliente_Id(clienteId);
    }

    @Override
    public Notificacion add(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    /**
     * Marca una notificación como leída actualizando el campo 'leida' a true.
     */
    @Override
    public Notificacion marcarLeida(Long id) {
        Notificacion found = notificacionRepository.findById(id).orElse(null);
        if (found == null) {
            throw new ResourceNotFoundException("Notificación con id: " + id + " no encontrada");
        }
        acceso.exigirDestinatario(found);
        found.setLeida(true);
        return notificacionRepository.save(found);
    }
}