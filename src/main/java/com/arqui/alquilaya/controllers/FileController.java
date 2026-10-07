package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controlador REST para la gestión de archivos (imágenes).
 * Permite subir fotos de perfil, fotos de propiedades y firmas de contratos.
 */
@RestController
@RequestMapping("/alquilaya")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Sube una foto de perfil (cliente o propietario).
     * @param file imagen a subir
     * @return JSON con la ruta del archivo guardado
     */
    @PostMapping("/archivos/perfil")
    public ResponseEntity<Map<String, String>> subirFotoPerfil(@RequestParam("file") MultipartFile file) {
        String ruta = fileStorageService.guardarArchivo(file, "perfiles");
        return new ResponseEntity<>(Map.of("url", ruta), HttpStatus.CREATED);
    }

    /**
     * Sube una foto de una propiedad.
     * @param file imagen a subir
     * @return JSON con la ruta del archivo guardado
     */
    @PostMapping("/archivos/propiedad")
    public ResponseEntity<Map<String, String>> subirFotoPropiedad(@RequestParam("file") MultipartFile file) {
        String ruta = fileStorageService.guardarArchivo(file, "propiedades");
        return new ResponseEntity<>(Map.of("url", ruta), HttpStatus.CREATED);
    }

    /**
     * Sube una imagen de firma para un contrato.
     * @param file imagen de la firma
     * @return JSON con la ruta del archivo guardado
     */
    @PostMapping("/archivos/firma")
    public ResponseEntity<Map<String, String>> subirFirma(@RequestParam("file") MultipartFile file) {
        String ruta = fileStorageService.guardarArchivo(file, "firmas");
        return new ResponseEntity<>(Map.of("url", ruta), HttpStatus.CREATED);
    }
}
