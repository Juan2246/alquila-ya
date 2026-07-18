package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.PropiedadClausula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropiedadClausulaRepository extends JpaRepository<PropiedadClausula, Long> {
    List<PropiedadClausula> findByPropiedad_Id(Long propiedadId);
}
