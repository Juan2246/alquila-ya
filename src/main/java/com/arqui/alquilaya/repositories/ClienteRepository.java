package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Cliente.
 * Incluye búsqueda por User asociado para obtener el perfil del cliente autenticado.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    public Cliente findByUser_Id(Long userId);
}
