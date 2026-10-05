package com.arqui.alquilaya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación AlquilaYa.
 * Los datos opcionales del perfil demo viven en {@link com.arqui.alquilaya.config.DataSeeder}.
 */
@SpringBootApplication
public class AlquilayaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlquilayaApplication.class, args);
    }
}
