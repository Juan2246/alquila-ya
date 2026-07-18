package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByPropiedad_Id(Long propiedadId);
    List<Resena> findByCliente_Id(Long clienteId);
}
