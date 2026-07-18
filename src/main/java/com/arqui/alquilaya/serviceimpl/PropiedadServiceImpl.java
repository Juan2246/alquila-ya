package com.arqui.alquilaya.serviceimpl;

import com.arqui.alquilaya.dtos.PropiedadDTO;
import com.arqui.alquilaya.entities.Comodidad;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.ComodidadRepository;
import com.arqui.alquilaya.repositories.PropiedadRepository;
import com.arqui.alquilaya.services.PropiedadService;
import com.arqui.alquilaya.services.PropietarioService;
import com.arqui.alquilaya.specifications.PropiedadSpecification;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de propiedades.
 * Gestiona la lógica de negocio para publicar, actualizar, listar y eliminar inmuebles.
 * Incluye buscador avanzado con filtros dinámicos y cotizador automático.
 */
@Service
public class PropiedadServiceImpl implements PropiedadService {

    @Autowired
    PropiedadRepository propiedadRepository;

    @Autowired
    PropietarioService propietarioService;

    @Autowired
    ComodidadRepository comodidadRepository;

    /**
     * Guarda una propiedad en la base de datos después de validar campos obligatorios.
     */
    @Override
    public Propiedad add(Propiedad propiedad) {
        if (propiedad.getTitulo() == null || propiedad.getTitulo().isBlank()) {
            throw new ValidationException("El título de la propiedad no puede estar vacío");
        }
        if (propiedad.getPropietario() == null) {
            throw new ValidationException("La propiedad debe tener un propietario asignado");
        }
        return propiedadRepository.save(propiedad);
    }

    @Override
    public Propiedad findById(Long id) {
        Propiedad found = propiedadRepository.findById(id).orElse(null);
        if (found == null) {
            throw new ResourceNotFoundException("Propiedad con id: " + id + " no encontrada");
        }
        return found;
    }

    @Override
    public List<Propiedad> listAll() {
        return propiedadRepository.findAll();
    }

    @Override
    public List<Propiedad> listByPropietarioId(Long propietarioId) {
        return propiedadRepository.findByPropietario_Id(propietarioId);
    }

    /**
     * Buscador avanzado con filtros dinámicos.
     * Permite filtrar por distrito, rango de precios y capacidad mínima de huéspedes.
     * Usa JPA Specifications para construir la query dinámicamente.
     */
    @Override
    public List<Propiedad> buscarConFiltros(String distrito, BigDecimal precioMin, BigDecimal precioMax, Integer capacidad) {
        Specification<Propiedad> spec = Specification
                .where(PropiedadSpecification.porDistrito(distrito))
                .and(PropiedadSpecification.porPrecioMinimo(precioMin))
                .and(PropiedadSpecification.porPrecioMaximo(precioMax))
                .and(PropiedadSpecification.porCapacidadMinima(capacidad));
        return propiedadRepository.findAll(spec);
    }

    /**
     * Crea una propiedad a partir de un DTO.
     * 1. Busca al propietario por su ID.
     * 2. Construye la entidad Propiedad con los datos del DTO (incluyendo coordenadas y capacidad).
     * 3. Asigna las comodidades si se proporcionan IDs.
     * 4. Asigna la fecha de publicación actual.
     * 5. Guarda y retorna el DTO con el ID generado.
     */
    @Override
    public PropiedadDTO addDTO(PropiedadDTO propiedadDTO) {
        Propietario propietario = propietarioService.findById(propiedadDTO.getPropietarioId());
        if (propietario == null) {
            throw new ResourceNotFoundException("Propietario con id: " + propiedadDTO.getPropietarioId() + " no encontrado");
        }

        // Buscar comodidades si se proporcionaron IDs
        List<Comodidad> comodidades = new ArrayList<>();
        if (propiedadDTO.getComodidadIds() != null && !propiedadDTO.getComodidadIds().isEmpty()) {
            comodidades = comodidadRepository.findAllById(propiedadDTO.getComodidadIds());
        }

        Propiedad newPropiedad = new Propiedad(
                null,
                propiedadDTO.getTitulo(),
                propiedadDTO.getDescripcion(),
                propiedadDTO.getUbicacion(),
                propiedadDTO.getDistrito(),
                propiedadDTO.getPrecio(),
                propiedadDTO.getHabitaciones(),
                propiedadDTO.getCapacidad(),
                propiedadDTO.getLatitud(),
                propiedadDTO.getLongitud(),
                LocalDateTime.now(),
                propietario,
                comodidades,
                null, null, null, null, null, null, null
        );

        newPropiedad = add(newPropiedad);
        propiedadDTO.setId(newPropiedad.getId());
        propiedadDTO.setPropietarioNombre(propietario.getNombre() + " " + propietario.getApellido());
        return propiedadDTO;
    }

    /**
     * Actualiza los campos de una propiedad existente.
     * Solo modifica los campos que no sean null en el objeto recibido.
     */
    @Override
    public Propiedad update(Propiedad propiedad) {
        Propiedad found = findById(propiedad.getId());

        if (propiedad.getTitulo() != null && !propiedad.getTitulo().isBlank()) {
            found.setTitulo(propiedad.getTitulo());
        }
        if (propiedad.getDescripcion() != null) {
            found.setDescripcion(propiedad.getDescripcion());
        }
        if (propiedad.getUbicacion() != null) {
            found.setUbicacion(propiedad.getUbicacion());
        }
        if (propiedad.getDistrito() != null) {
            found.setDistrito(propiedad.getDistrito());
        }
        if (propiedad.getPrecio() != null) {
            found.setPrecio(propiedad.getPrecio());
        }
        if (propiedad.getHabitaciones() != null) {
            found.setHabitaciones(propiedad.getHabitaciones());
        }
        if (propiedad.getCapacidad() != null) {
            found.setCapacidad(propiedad.getCapacidad());
        }
        if (propiedad.getLatitud() != null) {
            found.setLatitud(propiedad.getLatitud());
        }
        if (propiedad.getLongitud() != null) {
            found.setLongitud(propiedad.getLongitud());
        }
        return propiedadRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        if (findById(id) == null) {
            throw new ResourceNotFoundException("Propiedad con id: " + id + " no encontrada para eliminar");
        }
        propiedadRepository.deleteById(id);
    }
}
