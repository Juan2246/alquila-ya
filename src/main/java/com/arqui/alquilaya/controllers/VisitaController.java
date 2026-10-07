package com.arqui.alquilaya.controllers;

import org.springframework.web.bind.annotation.*;
import com.arqui.alquilaya.dtos.LecturasDTO;
import com.arqui.alquilaya.dtos.VisitaDTO;
import com.arqui.alquilaya.entities.Visita;
import com.arqui.alquilaya.services.VisitaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;

    /** Listar todas las visitas de un cliente. */
    @GetMapping("/visitas/cliente/{clienteId}")
    public ResponseEntity<List<LecturasDTO.VisitaLectura>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(visitaService.listByClienteId(id).stream().map(LecturasDTO::visita).toList(), HttpStatus.OK);
    }

    /** Listar todas las visitas a una propiedad. */
    @GetMapping("/visitas/propiedad/{propiedadId}")
    public ResponseEntity<List<LecturasDTO.VisitaLectura>> listByPropiedadId(@PathVariable("propiedadId") Long id) {
        return new ResponseEntity<>(visitaService.listByPropiedadId(id).stream().map(LecturasDTO::visita).toList(), HttpStatus.OK);
    }

    /** Crear una nueva visita (solo CLIENTE). */
    @PostMapping("/visitas")
    public ResponseEntity<VisitaDTO> add(@Valid @RequestBody VisitaDTO visitaDTO) {
        VisitaDTO newVisitaDTO = visitaService.addDTO(visitaDTO);
        return new ResponseEntity<>(newVisitaDTO, HttpStatus.CREATED);
    }

    /** Actualizar el estado de una visita (ej: marcar como COMPLETADA). */
    @PutMapping("/visitas")
    public ResponseEntity<LecturasDTO.VisitaLectura> update(@RequestBody Visita visita) {
        Visita updated = visitaService.update(visita);
        return new ResponseEntity<>(LecturasDTO.visita(updated), HttpStatus.OK);
    }
}