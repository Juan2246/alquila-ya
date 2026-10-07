package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.CotizacionDTO;
import com.arqui.alquilaya.dtos.PropiedadDTO;
import com.arqui.alquilaya.entities.Comodidad;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.PropiedadClausula;
import com.arqui.alquilaya.entities.PropiedadFoto;
import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.ComodidadRepository;
import com.arqui.alquilaya.repositories.PropiedadClausulaRepository;
import com.arqui.alquilaya.repositories.PropiedadFotoRepository;
import com.arqui.alquilaya.repositories.PropiedadRepository;
import com.arqui.alquilaya.security.AccesoActual;
import com.arqui.alquilaya.services.FileStorageService;
import com.arqui.alquilaya.services.PropiedadService;
import com.arqui.alquilaya.services.PropietarioService;
import com.arqui.alquilaya.specifications.PropiedadSpecification;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de propiedades.
 * Gestiona la lógica de negocio para publicar, actualizar, listar y eliminar inmuebles.
 * Incluye buscador avanzado con filtros dinámicos y cotizador automático.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PropiedadServiceImpl implements PropiedadService {

    private final PropiedadRepository propiedadRepository;

    private final PropietarioService propietarioService;

    private final ComodidadRepository comodidadRepository;

    private final AccesoActual acceso;
    private final PropiedadFotoRepository fotos;
    private final PropiedadClausulaRepository clausulas;
    private final FileStorageService archivos;

    @Override
    public PropiedadFoto agregarFoto(Long id, String url) {
        Propiedad propiedad = findById(id); acceso.exigirPropietario(propiedad.getPropietario());
        archivos.validarFotoPropia(url);
        var existente = propiedad.getFotos() == null ? Optional.<PropiedadFoto>empty()
                : propiedad.getFotos().stream().filter(f -> url.equals(f.getUrl())).findFirst();
        return existente.orElseGet(() -> fotos.save(new PropiedadFoto(null, url, propiedad)));
    }

    @Override
    public PropiedadClausula agregarClausula(Long id, String texto) {
        Propiedad propiedad = findById(id); acceso.exigirPropietario(propiedad.getPropietario());
        if (texto == null || texto.isBlank() || texto.length() > 250)
            throw new ValidationException("La cláusula debe tener entre 1 y 250 caracteres");
        var existente = propiedad.getClausulas() == null ? Optional.<PropiedadClausula>empty()
                : propiedad.getClausulas().stream().filter(c -> texto.trim().equals(c.getTexto())).findFirst();
        if (existente.isPresent()) return existente.get();
        return clausulas.save(new PropiedadClausula(null, texto.trim(), propiedad));
    }

    @Override
    public void eliminarClausula(Long id, Long clausulaId) {
        acceso.exigirPropietario(findById(id).getPropietario());
        var clausula = clausulas.findById(clausulaId).orElseThrow(() -> new ResourceNotFoundException("Cláusula no encontrada"));
        if (!id.equals(clausula.getPropiedad().getId())) throw new ResourceNotFoundException("Cláusula no encontrada en esta propiedad");
        clausulas.delete(clausula);
    }

    @Override
    public Propiedad updateDTO(PropiedadDTO entrada) {
        Propiedad datos = new Propiedad(); datos.setId(entrada.getId());
        datos.setTitulo(entrada.getTitulo()); datos.setDescripcion(entrada.getDescripcion());
        datos.setUbicacion(entrada.getUbicacion()); datos.setDistrito(entrada.getDistrito());
        datos.setPrecio(entrada.getPrecio()); datos.setHabitaciones(entrada.getHabitaciones());
        datos.setCapacidad(entrada.getCapacidad()); datos.setLatitud(entrada.getLatitud()); datos.setLongitud(entrada.getLongitud());
        Propiedad propiedad = update(datos);
        if (entrada.getComodidadIds() != null) {
            var seleccion = comodidadRepository.findAllById(entrada.getComodidadIds());
            if (seleccion.size() != new HashSet<>(entrada.getComodidadIds()).size())
                throw new ValidationException("Hay comodidades que no existen");
            propiedad.setComodidades(seleccion);
        }
        return propiedadRepository.save(propiedad);
    }

    /**
     * Guarda una propiedad en la base de datos después de validar campos obligatorios.
     */
    @Override
    public Propiedad add(Propiedad propiedad) {
        if (propiedad.getTitulo() == null || propiedad.getTitulo().isBlank()) {
            throw new ValidationException("El título de la propiedad no puede estar vacío");
        }
        if (propiedad.getId() != null) {
            throw new ValidationException("Una propiedad nueva no debe tener un ID");
        }
        if (propiedad.getPropietario() == null || propiedad.getPropietario().getId() == null) {
            throw new ValidationException("La propiedad debe tener un propietario asignado");
        }
        Propietario propietario = propietarioService.findById(propiedad.getPropietario().getId());
        acceso.exigirPropietario(propietario);
        propiedad.setPropietario(propietario);
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
     * Cotizador automático.
     * Recibe las fechas de un viaje y calcula el precio total a pagar.
     * Fórmula: noches × precio por noche.
     */
    @Override
    public CotizacionDTO cotizar(Long propiedadId, String checkIn, String checkOut) {
        Propiedad propiedad = findById(propiedadId);

        LocalDate fechaCheckIn = LocalDate.parse(checkIn);
        LocalDate fechaCheckOut = LocalDate.parse(checkOut);

        if (!fechaCheckOut.isAfter(fechaCheckIn)) {
            throw new ValidationException("La fecha de check-out debe ser posterior a la fecha de check-in");
        }

        long noches = ChronoUnit.DAYS.between(fechaCheckIn, fechaCheckOut);
        BigDecimal precioTotal = propiedad.getPrecio().multiply(BigDecimal.valueOf(noches));

        return new CotizacionDTO(
                propiedad.getId(),
                propiedad.getTitulo(),
                checkIn,
                checkOut,
                noches,
                propiedad.getPrecio(),
                precioTotal
        );
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
            if (comodidades.size() != new HashSet<>(propiedadDTO.getComodidadIds()).size())
                throw new ValidationException("Hay comodidades que no existen");
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
        acceso.exigirPropietario(found.getPropietario());

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
        if ((found.getPrecio() != null && found.getPrecio().signum() <= 0)
                || (found.getCapacidad() != null && found.getCapacidad() <= 0)
                || (found.getHabitaciones() != null && found.getHabitaciones() <= 0))
            throw new ValidationException("Precio, capacidad y habitaciones deben ser positivos");
        return propiedadRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        Propiedad found = findById(id);
        acceso.exigirPropietario(found.getPropietario());
        propiedadRepository.deleteById(id);
    }
}
