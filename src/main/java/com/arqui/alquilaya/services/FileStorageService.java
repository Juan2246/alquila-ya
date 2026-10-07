package com.arqui.alquilaya.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz de servicio para la gestión de archivos locales.
 * Permite subir imágenes (fotos de perfil, propiedades, firmas de contratos)
 * y las guarda en una carpeta local del servidor.
 */
public interface FileStorageService {
    void validarFotoPropia(String url);
    Resource leerFirma(String url);
    /**
     * Guarda un archivo en el subdirectorio especificado.
     * @param file archivo a guardar
     * @param subdirectorio carpeta dentro del directorio base (ej: "perfiles", "propiedades", "firmas")
     * @return la ruta relativa del archivo guardado para consumirla en el frontend
     */
    public String guardarArchivo(MultipartFile file, String subdirectorio);
}