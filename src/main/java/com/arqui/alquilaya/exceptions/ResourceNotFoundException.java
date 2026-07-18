package com.arqui.alquilaya.exceptions;

/**
 * Excepción personalizada que se lanza cuando un recurso solicitado no existe en la base de datos.
 * Extiende RuntimeException para que sea una excepción no verificada (unchecked),
 * lo que significa que no es obligatorio capturarla con try-catch.
 * El ExceptionController la intercepta globalmente y devuelve un HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    // Constructor vacío por defecto
    public ResourceNotFoundException() { super(); }

    // Constructor con mensaje personalizado para describir qué recurso no se encontró
    public ResourceNotFoundException(String message) { super(message); }
}
