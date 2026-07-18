package com.arqui.alquilaya.serviceimpl;

import com.arqui.alquilaya.entities.Notificacion;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.NotificacionRepository;
import com.arqui.alquilaya.services.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de notificaciones.
 * Gestiona el envío y marcado de notificaciones como leídas para los clientes.
 */
@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    NotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> listByClienteId(Long clienteId) {
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
        found.setLeida(true);
        return notificacionRepository.save(found);
    }
}
