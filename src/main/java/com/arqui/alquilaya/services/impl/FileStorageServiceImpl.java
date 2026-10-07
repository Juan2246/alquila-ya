package com.arqui.alquilaya.services.impl;

import java.nio.file.*;
import java.util.*;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.security.AccesoActual;
import com.arqui.alquilaya.services.FileStorageService;
import jakarta.validation.ValidationException;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Decodifica y recodifica imágenes: no confía en extensión ni Content-Type enviados. */
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${file.upload-dir:uploads}") private String uploadDir;
    private final AccesoActual acceso;

    @Override
    public String guardarArchivo(MultipartFile file, String carpeta) {
        if (!List.of("propiedades", "perfiles", "firmas").contains(carpeta)
                || file == null || file.isEmpty() || file.getSize() > 10 * 1024 * 1024)
            throw new ValidationException("Proporciona una imagen PNG o JPEG de hasta 10 MB");
        try (var entrada = ImageIO.createImageInputStream(file.getInputStream())) {
            var readers = ImageIO.getImageReaders(entrada);
            if (!readers.hasNext()) throw new ValidationException("El archivo no es una imagen válida");
            var reader = readers.next();
            try {
                String formato = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!List.of("png", "jpeg", "jpg").contains(formato))
                    throw new ValidationException("Solo se admiten imágenes PNG o JPEG");
                reader.setInput(entrada);
                int ancho = reader.getWidth(0), alto = reader.getHeight(0);
                if (ancho < 1 || alto < 1 || (long) ancho * alto > 20_000_000)
                    throw new ValidationException("La imagen supera los 20 megapíxeles");
                var imagen = reader.read(0);
                String extension = formato.equals("png") ? "png" : "jpg";
                String nombre = acceso.usuario().getUser().getId() + "-" + UUID.randomUUID() + "." + extension;
                Path dir = Path.of(uploadDir, carpeta).toAbsolutePath().normalize();
                Files.createDirectories(dir);
                if (!ImageIO.write(imagen, extension, dir.resolve(nombre).toFile()))
                    throw new IOException("No hay codificador de imagen");
                return "/uploads/" + carpeta + "/" + nombre;
            } finally { reader.dispose(); }
        } catch (IOException e) {
            throw new ValidationException("No se pudo procesar la imagen");
        }
    }

    @Override
    public void validarFotoPropia(String url) {
        String prefijo = "/uploads/propiedades/" + acceso.usuario().getUser().getId() + "-";
        if (url == null || !url.startsWith(prefijo))
            throw new AccessDeniedException("La foto no pertenece a tu cuenta");
        resolver(url, "propiedades");
    }

    private Path resolver(String url, String carpeta) {
        String prefijo = "/uploads/" + carpeta + "/";
        if (url == null || !url.startsWith(prefijo)) throw new ValidationException("Archivo no válido");
        String nombre = url.substring(prefijo.length());
        if (!nombre.matches("[0-9]+-[a-f0-9-]{36}\\.(png|jpg)")) throw new ValidationException("Archivo no válido");
        Path base = Path.of(uploadDir, carpeta).toAbsolutePath().normalize();
        Path archivo = base.resolve(nombre).normalize();
        if (!archivo.startsWith(base) || !Files.isRegularFile(archivo))
            throw new ResourceNotFoundException("Archivo no encontrado");
        return archivo;
    }

    @Override
    public Resource leerFirma(String url) { return new FileSystemResource(resolver(url, "firmas")); }
}