package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.ReservaDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.Reserva;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.ReservaRepository;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.PropiedadService;
import com.arqui.alquilaya.services.ReservaService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implementación del servicio de reservas.
 * Gestiona la creación de reservas con validación de solapamiento de fechas
 * y cálculo automático del precio total.
 */
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;

    private final ClienteService clienteService;

    private final PropiedadService propiedadService;

    @Override
    public Reserva findById(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva con id: " + id + " no encontrada"));
    }

    @Override
    public List<Reserva> listByClienteId(Long clienteId) {
        return reservaRepository.findByCliente_Id(clienteId);
    }

    @Override
    public List<Reserva> listByPropiedadId(Long propiedadId) {
        return reservaRepository.findByPropiedad_Id(propiedadId);
    }

    /**
     * Crea una nueva reserva a partir de un DTO.
     * Flujo:
     * 1. Buscar cliente y propiedad.
     * 2. Validar que las fechas sean correctas (check-in < check-out, ambas en el futuro).
     * 3. Validar que no haya solapamiento con reservas existentes.
     * 4. Calcular precio total (noches × precio base).
     * 5. Guardar la reserva con estado PENDIENTE.
     */
    @Override
    public ReservaDTO addDTO(ReservaDTO reservaDTO) {
        // Paso 1: Buscar cliente y propiedad
        Cliente cliente = clienteService.findById(reservaDTO.getClienteId());
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id: " + reservaDTO.getClienteId() + " no encontrado");
        }
        Propiedad propiedad = propiedadService.findById(reservaDTO.getPropiedadId());

        // Paso 2: Validar fechas
        LocalDate checkIn = LocalDate.parse(reservaDTO.getFechaCheckIn());
        LocalDate checkOut = LocalDate.parse(reservaDTO.getFechaCheckOut());

        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("La fecha de check-out debe ser posterior a la fecha de check-in");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ValidationException("La fecha de check-in no puede ser en el pasado");
        }

        // Paso 3: Validar solapamiento de fechas
        List<Reserva> solapadas = reservaRepository.findReservasSolapadas(
                propiedad.getId(), checkIn, checkOut
        );
        if (!solapadas.isEmpty()) {
            throw new ValidationException(
                    "Las fechas seleccionadas se solapan con una reserva existente para esta propiedad. " +
                    "Por favor seleccione otras fechas."
            );
        }

        // Paso 4: Calcular precio total
        long noches = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal precioTotal = propiedad.getPrecio().multiply(BigDecimal.valueOf(noches));

        // Paso 5: Crear y guardar la reserva
        Reserva newReserva = new Reserva(
                null, checkIn, checkOut,
                "PENDIENTE", precioTotal,
                LocalDateTime.now(),
                cliente, propiedad
        );
        newReserva = reservaRepository.save(newReserva);

        // Mapear respuesta
        reservaDTO.setId(newReserva.getId());
        reservaDTO.setEstado(newReserva.getEstado());
        reservaDTO.setPrecioTotal(precioTotal);
        reservaDTO.setClienteNombre(cliente.getNombre() + " " + cliente.getApellido());
        reservaDTO.setPropiedadTitulo(propiedad.getTitulo());
        return reservaDTO;
    }

    /**
     * Actualiza el estado de una reserva existente.
     */
    @Override
    public Reserva update(Reserva reserva) {
        Reserva found = findById(reserva.getId());
        if (reserva.getEstado() != null) {
            found.setEstado(reserva.getEstado());
        }
        return reservaRepository.save(found);
    }
}
