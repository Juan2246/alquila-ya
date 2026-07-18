package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.PropiedadDTO;
import com.arqui.alquilaya.entities.Propiedad;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de propiedades inmobiliarias.
 * Define operaciones CRUD, consultas por propietario y buscador avanzado con filtros.
 */
public interface PropiedadService {
    public Propiedad add(Propiedad propiedad);
    public Propiedad findById(Long id);
    public List<Propiedad> listAll();
    public List<Propiedad> listByPropietarioId(Long propietarioId);
    public PropiedadDTO addDTO(PropiedadDTO propiedadDTO);
    public Propiedad update(Propiedad propiedad);
    public void delete(Long id);

    /**
     * Buscador avanzado con filtros dinámicos.
     * Todos los parámetros son opcionales; si son null, no se aplican.
     * @param distrito filtro por distrito (case-insensitive)
     * @param precioMin precio mínimo del rango
     * @param precioMax precio máximo del rango
     * @param capacidad capacidad mínima de huéspedes
     * @return lista de propiedades que cumplen todos los filtros
     */
    public List<Propiedad> buscarConFiltros(String distrito, BigDecimal precioMin, BigDecimal precioMax, Integer capacidad);
}
