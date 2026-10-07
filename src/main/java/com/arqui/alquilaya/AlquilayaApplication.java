package com.arqui.alquilaya;

import com.arqui.alquilaya.config.DataSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación AlquilaYa.
 * Los datos opcionales del perfil demo viven en {@link DataSeeder}.
 */
@SpringBootApplication
public class AlquilayaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlquilayaApplication.class, args);
    }
}