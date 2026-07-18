package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Rol.
 * Hereda de JpaRepository, que provee métodos CRUD estándar (save, findById, findAll, delete).
 * Se añade un query method personalizado para buscar roles por nombre.
 */
public interface RolRepository extends JpaRepository<Rol, Long> {

    // Query Method: Spring Data genera automáticamente la consulta SQL a partir del nombre del método
    public Rol findByNombre(String nombre);
}
