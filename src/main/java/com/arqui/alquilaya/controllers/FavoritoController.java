package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.FavoritoDTO;
import com.arqui.alquilaya.entities.Favorito;
import com.arqui.alquilaya.services.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
public class FavoritoController {

    @Autowired
    FavoritoService favoritoService;

    @GetMapping("/favoritos/cliente/{clienteId}")
    public ResponseEntity<List<Favorito>> listByClienteId(@PathVariable("clienteId") Long id) {
        return new ResponseEntity<>(favoritoService.listByClienteId(id), HttpStatus.OK);
    }

    @PostMapping("/favoritos")
    public ResponseEntity<FavoritoDTO> add(@RequestBody FavoritoDTO favoritoDTO) {
        FavoritoDTO newFavoritoDTO = favoritoService.addDTO(favoritoDTO);
        return new ResponseEntity<>(newFavoritoDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/favoritos/{favoritoId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("favoritoId") Long id) {
        favoritoService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
