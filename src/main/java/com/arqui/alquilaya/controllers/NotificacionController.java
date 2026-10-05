package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.entities.Notificacion;
import com.arqui.alquilaya.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/notificaciones/cliente/{clienteId}")
    public ResponseEntity<List<Notificacion>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(notificacionService.listByClienteId(id), HttpStatus.OK);
    }

    /** Marcar una notificación como leída. */
    @PutMapping("/notificaciones/{notificacionId}/leer")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable("notificacionId") Long id) {
        Notificacion updated = notificacionService.marcarLeida(id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
