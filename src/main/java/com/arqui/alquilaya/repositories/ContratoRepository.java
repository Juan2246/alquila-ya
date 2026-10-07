package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Contrato;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Contrato c where c.id = :id")
    Optional<Contrato> bloquearPorId(@Param("id") Long id);
    Optional<Contrato> findByReserva_Id(Long reservaId);
    List<Contrato> findByCliente_User_IdOrPropiedad_Propietario_User_Id(Long clienteUserId, Long propietarioUserId);
    List<Contrato> findByCliente_Id(Long clienteId);
    List<Contrato> findByPropiedad_Id(Long propiedadId);
}