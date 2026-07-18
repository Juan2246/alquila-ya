package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.TicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketSoporteRepository extends JpaRepository<TicketSoporte, Long> {
    List<TicketSoporte> findByCliente_Id(Long clienteId);
}
