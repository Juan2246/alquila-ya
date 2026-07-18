package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Visita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio para la entidad Visita.
 * Contiene queries esenciales para la regla de negocio de reseñas:
 * findByCliente_IdAndPropiedad_IdAndEstado verifica que exista una visita COMPLETADA.
 */
public interface VisitaRepository extends JpaRepository<Visita, Long> {

    List<Visita> findByCliente_Id(Long clienteId);
    List<Visita> findByPropiedad_Id(Long propiedadId);

    // Query crítico: busca visitas de un cliente a una propiedad con un estado específico
    List<Visita> findByCliente_IdAndPropiedad_IdAndEstado(Long clienteId, Long propiedadId, String estado);
}
