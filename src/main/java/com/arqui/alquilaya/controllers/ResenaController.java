package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.ResenaDTO;
import com.arqui.alquilaya.entities.Resena;
import com.arqui.alquilaya.services.ResenaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    /** Listar reseñas de una propiedad específica. */
    @GetMapping("/resenas/propiedad/{propiedadId}")
    public ResponseEntity<List<Resena>> listByPropiedadId(@PathVariable("propiedadId") Long id) {
        return new ResponseEntity<>(resenaService.listByPropiedadId(id), HttpStatus.OK);
    }

    /**
     * Crear una nueva reseña (solo CLIENTE).
     * Solo se crea si se valida en el servicio que el cliente tenga una visita COMPLETADA.
     */
    @PostMapping("/resenas")
    public ResponseEntity<ResenaDTO> add(@RequestBody ResenaDTO resenaDTO) {
        ResenaDTO newResenaDTO = resenaService.addDTO(resenaDTO);
        return new ResponseEntity<>(newResenaDTO, HttpStatus.CREATED);
    }
}
