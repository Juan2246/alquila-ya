package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.entities.Notificacion;
import com.arqui.alquilaya.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.arqui.alquilaya.dtos.LecturasDTO;


@RestController
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/notificaciones/cliente/{clienteId}")
    public ResponseEntity<List<LecturasDTO.NotificacionLectura>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(notificacionService.listByClienteId(id).stream().map(LecturasDTO::notificacion).toList(), HttpStatus.OK);
    }

    @GetMapping("/notificaciones/propietario/{id}")
    public List<LecturasDTO.NotificacionLectura> porPropietario(@PathVariable Long id) {
        return notificacionService.listByPropietarioId(id).stream().map(LecturasDTO::notificacion).toList();
    }

    /** Marcar una notificación como leída. */
    @PutMapping("/notificaciones/{notificacionId}/leer")
    public ResponseEntity<LecturasDTO.NotificacionLectura> marcarLeida(@PathVariable("notificacionId") Long id) {
        Notificacion updated = notificacionService.marcarLeida(id);
        return new ResponseEntity<>(LecturasDTO.notificacion(updated), HttpStatus.OK);
    }
}
