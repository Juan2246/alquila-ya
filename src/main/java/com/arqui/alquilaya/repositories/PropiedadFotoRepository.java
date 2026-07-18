package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.PropiedadFoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropiedadFotoRepository extends JpaRepository<PropiedadFoto, Long> {
    List<PropiedadFoto> findByPropiedad_Id(Long propiedadId);
}
