package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Propiedad;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio para la entidad Propiedad.
 * Extiende JpaSpecificationExecutor para soportar búsquedas dinámicas con filtros.
 */
public interface PropiedadRepository extends JpaRepository<Propiedad, Long>, JpaSpecificationExecutor<Propiedad> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Propiedad p where p.id = :id")
    Optional<Propiedad> bloquearPorId(@Param("id") Long id);

    // Buscar todas las propiedades de un propietario específico
    List<Propiedad> findByPropietario_Id(Long propietarioId);

    // Buscar propiedades por distrito para filtrado geográfico
    List<Propiedad> findByDistrito(String distrito);
}