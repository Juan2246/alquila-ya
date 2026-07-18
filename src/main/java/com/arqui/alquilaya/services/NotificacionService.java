package com.arqui.alquilaya.services;

import com.arqui.alquilaya.entities.Notificacion;
import java.util.List;

public interface NotificacionService {
    public List<Notificacion> listByClienteId(Long clienteId);
    public Notificacion add(Notificacion notificacion);
    public Notificacion marcarLeida(Long id);
}
