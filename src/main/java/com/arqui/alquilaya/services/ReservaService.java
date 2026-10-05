package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.ReservaDTO;
import com.arqui.alquilaya.dtos.DisponibilidadDTO;
import com.arqui.alquilaya.entities.Reserva;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de reservas.
 * Incluye validación de solapamiento de fechas.
 */
public interface ReservaService {
    public List<DisponibilidadDTO> disponibilidad(Long propiedadId);
    public Reserva findById(Long id);
    public List<Reserva> listByClienteId(Long clienteId);
    public List<Reserva> listByPropiedadId(Long propiedadId);
    public ReservaDTO addDTO(ReservaDTO reservaDTO);
    public Reserva update(Reserva reserva);
}
