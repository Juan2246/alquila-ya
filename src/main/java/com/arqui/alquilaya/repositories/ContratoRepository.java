package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    List<Contrato> findByCliente_Id(Long clienteId);
    List<Contrato> findByPropiedad_Id(Long propiedadId);
}
