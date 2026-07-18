package com.arqui.alquilaya.serviceimpl;

import com.arqui.alquilaya.services.FileStorageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementación del servicio de almacenamiento de archivos.
 * Guarda archivos en una carpeta local del servidor y devuelve la ruta relativa.
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Crea el directorio base de uploads al iniciar la aplicación si no existe.
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + uploadDir, e);
        }
    }

    /**
     * Guarda un archivo en el subdirectorio especificado.
     * Genera un nombre único con UUID para evitar colisiones.
     * @return la ruta relativa del archivo (ej: "/uploads/propiedades/uuid-nombre.jpg")
     */
    @Override
    public String guardarArchivo(MultipartFile file, String subdirectorio) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("El archivo está vacío o no fue proporcionado");
        }

        try {
            // Crear subdirectorio si no existe
            Path dirPath = Paths.get(uploadDir, subdirectorio);
            Files.createDirectories(dirPath);

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Guardar el archivo
            Path filePath = dirPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Devolver la ruta relativa para el frontend
            return "/uploads/" + subdirectorio + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }
}
