package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Propietario.
 * Permite buscar un propietario por el ID del User asociado (para vincular autenticación con perfil).
 */
public interface PropietarioRepository extends JpaRepository<Propietario, Long> {

    public Propietario findByUser_Id(Long userId);
}
