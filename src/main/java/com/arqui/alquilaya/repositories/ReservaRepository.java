package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.dtos.DisponibilidadDTO;
import com.arqui.alquilaya.entities.Reserva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio para la entidad Reserva.
 * Incluye query JPQL para validar solapamiento de fechas.
 */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT new com.arqui.alquilaya.dtos.DisponibilidadDTO(r.fechaCheckIn, r.fechaCheckOut, r.estado) " +
            "FROM Reserva r WHERE r.propiedad.id = :propiedadId " +
            "AND r.estado IN ('PENDIENTE', 'CONFIRMADA') ORDER BY r.fechaCheckIn")
    List<DisponibilidadDTO> disponibilidad(@Param("propiedadId") Long propiedadId);

    List<Reserva> findByCliente_Id(Long clienteId);
    List<Reserva> findByPropiedad_Id(Long propiedadId);

    // Busca reservas de un cliente en una propiedad con estado específico
    List<Reserva> findByCliente_IdAndPropiedad_IdAndEstado(Long clienteId, Long propiedadId, String estado);

    /**
     * Valida solapamiento de fechas para una propiedad.
     * Una reserva se solapa si su check-in es antes del check-out solicitado
     * Y su check-out es después del check-in solicitado.
     * Solo considera reservas activas (PENDIENTE o CONFIRMADA).
     */
    @Query("SELECT r FROM Reserva r WHERE r.propiedad.id = :propiedadId " +
           "AND r.estado IN ('PENDIENTE', 'CONFIRMADA') " +
           "AND r.fechaCheckIn < :checkOut " +
           "AND r.fechaCheckOut > :checkIn")
    List<Reserva> findReservasSolapadas(
            @Param("propiedadId") Long propiedadId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
