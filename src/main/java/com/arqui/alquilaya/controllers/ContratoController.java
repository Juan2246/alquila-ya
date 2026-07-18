package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.ContratoDTO;
import com.arqui.alquilaya.entities.Contrato;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.services.ContratoService;
import com.arqui.alquilaya.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
public class ContratoController {

    @Autowired
    ContratoService contratoService;

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/contratos")
    public ResponseEntity<List<Contrato>> listAll() {
        return new ResponseEntity<>(contratoService.listAll(), HttpStatus.OK);
    }

    @GetMapping("/contratos/{contratoId}")
    public ResponseEntity<Contrato> findById(@PathVariable("contratoId") Long id) {
        Contrato found = contratoService.findById(id);
        if (found == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    @GetMapping("/contratos/cliente/{clienteId}")
    public ResponseEntity<List<Contrato>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(contratoService.listByClienteId(id), HttpStatus.OK);
    }

    @PostMapping("/contratos")
    public ResponseEntity<ContratoDTO> add(@RequestBody ContratoDTO contratoDTO) {
        ContratoDTO newContratoDTO = contratoService.addDTO(contratoDTO);
        return new ResponseEntity<>(newContratoDTO, HttpStatus.CREATED);
    }

    @PutMapping("/contratos")
    public ResponseEntity<Contrato> update(@RequestBody Contrato contrato) {
        Contrato updated = contratoService.update(contrato);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * Adjunta una imagen de firma a un contrato existente.
     * Sube la imagen usando el FileStorageService y actualiza el campo firmaImagenUrl del contrato.
     * @param id ID del contrato a firmar
     * @param file imagen de la firma
     * @return el contrato actualizado con la firma adjunta
     */
    @PostMapping("/contratos/{contratoId}/firmar")
    public ResponseEntity<Contrato> firmarContrato(
            @PathVariable("contratoId") Long id,
            @RequestParam("file") MultipartFile file
    ) {
        Contrato contrato = contratoService.findById(id);
        if (contrato == null) {
            throw new ResourceNotFoundException("Contrato con id: " + id + " no encontrado");
        }

        // Subir la imagen de firma
        String rutaFirma = fileStorageService.guardarArchivo(file, "firmas");

        // Actualizar el contrato con la ruta de la firma
        contrato.setFirmaImagenUrl(rutaFirma);
        contrato.setEstado("FIRMADO");
        Contrato updated = contratoService.update(contrato);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
