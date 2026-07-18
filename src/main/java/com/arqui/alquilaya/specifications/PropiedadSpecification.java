package com.arqui.alquilaya.specifications;

import com.arqui.alquilaya.entities.Propiedad;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Clase utilitaria que construye Specifications para filtrado dinámico de propiedades.
 * Permite combinar múltiples filtros: distrito, rango de precios y capacidad.
 */
public class PropiedadSpecification {

    /**
     * Filtra propiedades por distrito (coincidencia exacta, case-insensitive).
     */
    public static Specification<Propiedad> porDistrito(String distrito) {
        return (root, query, cb) -> {
            if (distrito == null || distrito.isBlank()) return null;
            return cb.equal(cb.lower(root.get("distrito")), distrito.toLowerCase());
        };
    }

    /**
     * Filtra propiedades con precio mayor o igual al mínimo.
     */
    public static Specification<Propiedad> porPrecioMinimo(BigDecimal precioMin) {
        return (root, query, cb) -> {
            if (precioMin == null) return null;
            return cb.greaterThanOrEqualTo(root.get("precio"), precioMin);
        };
    }

    /**
     * Filtra propiedades con precio menor o igual al máximo.
     */
    public static Specification<Propiedad> porPrecioMaximo(BigDecimal precioMax) {
        return (root, query, cb) -> {
            if (precioMax == null) return null;
            return cb.lessThanOrEqualTo(root.get("precio"), precioMax);
        };
    }

    /**
     * Filtra propiedades con capacidad mayor o igual a la solicitada.
     */
    public static Specification<Propiedad> porCapacidadMinima(Integer capacidad) {
        return (root, query, cb) -> {
            if (capacidad == null) return null;
            return cb.greaterThanOrEqualTo(root.get("capacidad"), capacidad);
        };
    }
}
