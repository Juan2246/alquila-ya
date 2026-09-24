package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.VisitaDTO;
import com.arqui.alquilaya.entities.Visita;
import com.arqui.alquilaya.services.VisitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;

    /** Listar todas las visitas de un cliente. */
    @GetMapping("/visitas/cliente/{clienteId}")
    public ResponseEntity<List<Visita>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(visitaService.listByClienteId(id), HttpStatus.OK);
    }

    /** Listar todas las visitas a una propiedad. */
    @GetMapping("/visitas/propiedad/{propiedadId}")
    public ResponseEntity<List<Visita>> listByPropiedadId(@PathVariable("propiedadId") Long id) {
        return new ResponseEntity<>(visitaService.listByPropiedadId(id), HttpStatus.OK);
    }

    /** Crear una nueva visita (solo CLIENTE). */
    @PostMapping("/visitas")
    public ResponseEntity<VisitaDTO> add(@RequestBody VisitaDTO visitaDTO) {
        VisitaDTO newVisitaDTO = visitaService.addDTO(visitaDTO);
        return new ResponseEntity<>(newVisitaDTO, HttpStatus.CREATED);
    }

    /** Actualizar el estado de una visita (ej: marcar como COMPLETADA). */
    @PutMapping("/visitas")
    public ResponseEntity<Visita> update(@RequestBody Visita visita) {
        Visita updated = visitaService.update(visita);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
