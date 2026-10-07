package com.arqui.alquilaya.controllers;

import org.springframework.web.bind.annotation.*;
import com.arqui.alquilaya.dtos.LecturasDTO;
import com.arqui.alquilaya.dtos.PagoDTO;
import com.arqui.alquilaya.entities.Pago;
import com.arqui.alquilaya.services.PagoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/pagos/contrato/{contratoId}")
    public ResponseEntity<List<LecturasDTO.PagoLectura>> listByContratoId(@PathVariable("contratoId") Long id) {
        return new ResponseEntity<>(pagoService.listByContratoId(id).stream().map(LecturasDTO::pago).toList(), HttpStatus.OK);
    }

    @PostMapping("/pagos")
    public ResponseEntity<PagoDTO> add(@Valid @RequestBody PagoDTO pagoDTO) {
        PagoDTO newPagoDTO = pagoService.addDTO(pagoDTO);
        return new ResponseEntity<>(newPagoDTO, HttpStatus.CREATED);
    }
}