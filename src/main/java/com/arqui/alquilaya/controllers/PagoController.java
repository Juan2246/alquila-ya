package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.PagoDTO;
import com.arqui.alquilaya.entities.Pago;
import com.arqui.alquilaya.services.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/pagos/contrato/{contratoId}")
    public ResponseEntity<List<Pago>> listByContratoId(@PathVariable("contratoId") Long id) {
        return new ResponseEntity<>(pagoService.listByContratoId(id), HttpStatus.OK);
    }

    @PostMapping("/pagos")
    public ResponseEntity<PagoDTO> add(@RequestBody PagoDTO pagoDTO) {
        PagoDTO newPagoDTO = pagoService.addDTO(pagoDTO);
        return new ResponseEntity<>(newPagoDTO, HttpStatus.CREATED);
    }
}
