package com.arqui.alquilaya.controllers;

import org.springframework.web.bind.annotation.*;
import com.arqui.alquilaya.dtos.LecturasDTO;
import com.arqui.alquilaya.dtos.ResenaDTO;
import com.arqui.alquilaya.entities.Resena;
import com.arqui.alquilaya.services.ResenaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    /** Listar reseñas de una propiedad específica. */
    @GetMapping("/resenas/propiedad/{propiedadId}")
    public ResponseEntity<List<LecturasDTO.ResenaLectura>> listByPropiedadId(@PathVariable("propiedadId") Long id) {
        return new ResponseEntity<>(resenaService.listByPropiedadId(id).stream().map(LecturasDTO::resena).toList(), HttpStatus.OK);
    }

    public record RespuestaEntrada(String respuesta) {}

    @PutMapping("/resenas/{id}/responder")
    public LecturasDTO.ResenaLectura responder(@PathVariable Long id, @RequestBody RespuestaEntrada entrada) {
        return LecturasDTO.resena(resenaService.responder(id, entrada.respuesta()));
    }

    /**
     * Crear una nueva reseña (solo CLIENTE).
     * Solo se crea si se valida en el servicio que el cliente tenga una visita COMPLETADA.
     */
    @PostMapping("/resenas")
    public ResponseEntity<LecturasDTO.ResenaLectura> add(@Valid @RequestBody ResenaDTO resenaDTO) {
        ResenaDTO newResenaDTO = resenaService.addDTO(resenaDTO);
        return new ResponseEntity<>(LecturasDTO.resena(resenaService.findById(newResenaDTO.getId())), HttpStatus.CREATED);
    }
}