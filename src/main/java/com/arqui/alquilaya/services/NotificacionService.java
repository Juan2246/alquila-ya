package com.arqui.alquilaya.services;

import com.arqui.alquilaya.entities.Notificacion;
import java.util.List;

public interface NotificacionService {
    List<Notificacion> listByPropietarioId(Long id);
    public List<Notificacion> listByClienteId(Long clienteId);
    public Notificacion add(Notificacion notificacion);
    public Notificacion marcarLeida(Long id);
}
