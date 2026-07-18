package com.arqui.alquilaya.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionMessage {

    private int status;                // Código HTTP del error (404, 406, etc.)
    private String exception;          // Nombre de la clase de excepción que se lanzó
    private String message;            // Descripción específica del error
    private String requestDescription; // URI del request que provocó el error
    private LocalDateTime timestamp;   // Momento exacto en que ocurrió el error
}
