package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByPropietario_Id(Long propietarioId);
    List<Notificacion> findByCliente_Id(Long clienteId);
}
