package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.CotizacionDTO;
import com.arqui.alquilaya.dtos.PropiedadDTO;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.PropiedadClausula;
import com.arqui.alquilaya.entities.PropiedadFoto;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de propiedades inmobiliarias.
 * Define operaciones CRUD, consultas por propietario y buscador avanzado con filtros.
 */
public interface PropiedadService {
    Propiedad updateDTO(PropiedadDTO entrada);
    PropiedadFoto agregarFoto(Long id, String url);
    PropiedadClausula agregarClausula(Long id, String texto);
    void eliminarClausula(Long propiedadId, Long clausulaId);
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

    /**
     * Cotizador automático: calcula el precio de una estadía.
     * Fórmula: noches × precio por noche.
     * @param propiedadId propiedad a cotizar
     * @param checkIn fecha de entrada (ISO, yyyy-MM-dd)
     * @param checkOut fecha de salida (ISO, yyyy-MM-dd), posterior al check-in
     * @return detalle de la cotización con noches y precio total
     */
    public CotizacionDTO cotizar(Long propiedadId, String checkIn, String checkOut);
}