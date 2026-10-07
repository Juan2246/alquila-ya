package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.services.FileStorageService;
import org.springframework.web.bind.annotation.*;
import com.arqui.alquilaya.dtos.ContratoDTO;
import com.arqui.alquilaya.dtos.LecturasDTO;
import com.arqui.alquilaya.entities.Contrato;
import com.arqui.alquilaya.services.ContratoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;

    @GetMapping("/contratos")
    public ResponseEntity<List<LecturasDTO.ContratoLectura>> listAll() {
        return new ResponseEntity<>(contratoService.listAll().stream().map(LecturasDTO::contrato).toList(), HttpStatus.OK);
    }

    @GetMapping("/contratos/{contratoId}")
    public ResponseEntity<LecturasDTO.ContratoLectura> findById(@PathVariable("contratoId") Long id) {
        Contrato found = contratoService.findById(id);
        if (found == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(LecturasDTO.contrato(found), HttpStatus.OK);
    }

    @GetMapping("/contratos/cliente/{clienteId}")
    public ResponseEntity<List<LecturasDTO.ContratoLectura>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(contratoService.listByClienteId(id).stream().map(LecturasDTO::contrato).toList(), HttpStatus.OK);
    }

    @GetMapping("/contratos/propiedad/{id}")
    public List<LecturasDTO.ContratoLectura> porPropiedad(@PathVariable Long id) {
        return contratoService.listByPropiedadId(id).stream().map(LecturasDTO::contrato).toList();
    }

    @GetMapping("/contratos/{id}/firma")
    public ResponseEntity<Resource> firma(@PathVariable Long id) {
        var archivo = contratoService.leerFirma(id);
        var tipo = archivo.getFilename().endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(tipo).header("Cache-Control", "no-store")
                .header("X-Content-Type-Options", "nosniff").body(archivo);
    }

    @PostMapping("/contratos")
    public ResponseEntity<ContratoDTO> add(@RequestBody ContratoDTO contratoDTO) {
        ContratoDTO newContratoDTO = contratoService.addDTO(contratoDTO);
        return new ResponseEntity<>(newContratoDTO, HttpStatus.CREATED);
    }

    @PutMapping("/contratos")
    public ResponseEntity<LecturasDTO.ContratoLectura> update(@RequestBody Contrato contrato) {
        Contrato updated = contratoService.update(contrato);
        return new ResponseEntity<>(LecturasDTO.contrato(updated), HttpStatus.OK);
    }

    /**
     * Adjunta una imagen de firma a un contrato existente.
     * Sube la imagen usando el FileStorageService y actualiza el campo firmaImagenUrl del contrato.
     * @param id ID del contrato a firmar
     * @param file imagen de la firma
     * @return el contrato actualizado con la firma adjunta
     */
    @PostMapping("/contratos/{contratoId}/firmar")
    public ResponseEntity<LecturasDTO.ContratoLectura> firmarContrato(
            @PathVariable("contratoId") Long id,
            @RequestParam("file") MultipartFile file
    ) {
        Contrato firmado = contratoService.firmar(id, file);
        return new ResponseEntity<>(LecturasDTO.contrato(firmado), HttpStatus.OK);
    }
}