package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.ReservaDTO;
import com.arqui.alquilaya.dtos.DisponibilidadDTO;
import com.arqui.alquilaya.entities.Reserva;
import com.arqui.alquilaya.services.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de reservas.
 * Solo CLIENTE puede crear reservas (configurado en SecurityConfiguration).
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    /** Listar todas las reservas de un cliente. */
    @GetMapping("/reservas/cliente/{clienteId}")
    public ResponseEntity<List<Reserva>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(reservaService.listByClienteId(id), HttpStatus.OK);
    }

    /** Detalles de reservas: solo el propietario del inmueble. */
    @GetMapping("/reservas/propiedad/{propiedadId}")
    public ResponseEntity<List<Reserva>> listByPropiedadId(@PathVariable("propiedadId") Long id) {
        return new ResponseEntity<>(reservaService.listByPropiedadId(id), HttpStatus.OK);
    }

    /** Disponibilidad para clientes, sin revelar quién reservó. */
    @GetMapping("/reservas/propiedad/{propiedadId}/disponibilidad")
    public ResponseEntity<List<DisponibilidadDTO>> disponibilidad(@PathVariable("propiedadId") Long id) {
        return ResponseEntity.ok(reservaService.disponibilidad(id));
    }

    /** Buscar una reserva por su ID. */
    @GetMapping("/reservas/{reservaId}")
    public ResponseEntity<Reserva> findById(@PathVariable("reservaId") Long id) {
        Reserva found = reservaService.findById(id);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    /** Crear una nueva reserva (solo CLIENTE). */
    @PostMapping("/reservas")
    public ResponseEntity<ReservaDTO> add(@RequestBody ReservaDTO reservaDTO) {
        ReservaDTO newReservaDTO = reservaService.addDTO(reservaDTO);
        return new ResponseEntity<>(newReservaDTO, HttpStatus.CREATED);
    }

    /** Actualizar estado de una reserva (ej: CONFIRMADA, COMPLETADA, CANCELADA). */
    @PutMapping("/reservas")
    public ResponseEntity<Reserva> update(@RequestBody Reserva reserva) {
        Reserva updated = reservaService.update(reserva);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
