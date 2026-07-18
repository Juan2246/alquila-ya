package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.ResenaDTO;
import com.arqui.alquilaya.entities.Resena;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de reseñas.
 * IMPORTANTE: La implementación (ResenaServiceImpl) contiene la validación
 * de que el cliente debe tener una visita completada antes de crear una reseña.
 */
public interface ResenaService {
    public Resena findById(Long id);
    public List<Resena> listByPropiedadId(Long propiedadId);
    public ResenaDTO addDTO(ResenaDTO resenaDTO);
}
