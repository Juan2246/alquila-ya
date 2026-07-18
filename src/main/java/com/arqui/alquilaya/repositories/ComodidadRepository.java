package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Comodidad;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Comodidad.
 */
public interface ComodidadRepository extends JpaRepository<Comodidad, Long> {
    Comodidad findByNombre(String nombre);
}
