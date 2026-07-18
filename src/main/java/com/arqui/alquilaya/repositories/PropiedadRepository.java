package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Repositorio para la entidad Propiedad.
 * Extiende JpaSpecificationExecutor para soportar búsquedas dinámicas con filtros.
 */
public interface PropiedadRepository extends JpaRepository<Propiedad, Long>, JpaSpecificationExecutor<Propiedad> {

    // Buscar todas las propiedades de un propietario específico
    List<Propiedad> findByPropietario_Id(Long propietarioId);

    // Buscar propiedades por distrito para filtrado geográfico
    List<Propiedad> findByDistrito(String distrito);
}
