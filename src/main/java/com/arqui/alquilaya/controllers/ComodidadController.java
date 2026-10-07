package com.arqui.alquilaya.controllers;
import com.arqui.alquilaya.dtos.LecturasDTO;
import com.arqui.alquilaya.services.ComodidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/alquilaya/comodidades")
@RequiredArgsConstructor
public class ComodidadController {
    private final ComodidadService comodidades;
    @GetMapping
    public List<LecturasDTO.ComodidadLectura> listAll() { return comodidades.listAll().stream().map(LecturasDTO::comodidad).toList(); }
}
