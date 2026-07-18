package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.VisitaDTO;
import com.arqui.alquilaya.entities.Visita;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de visitas a propiedades.
 */
public interface VisitaService {
    public Visita findById(Long id);
    public List<Visita> listByClienteId(Long clienteId);
    public List<Visita> listByPropiedadId(Long propiedadId);
    public VisitaDTO addDTO(VisitaDTO visitaDTO);
    public Visita update(Visita visita);
}
