package com.arqui.alquilaya.exceptions;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;


@RestControllerAdvice
public class ExceptionController {

    /**
     * Captura excepciones de validación (campos vacíos, datos inválidos, etc.).
     * Devuelve HTTP 406 (NOT_ACCEPTABLE) con los detalles del error.
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ExceptionMessage validationException(ValidationException e, WebRequest r) {
        return new ExceptionMessage(
                HttpStatus.NOT_ACCEPTABLE.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                r.getDescription(false),
                LocalDateTime.now()
        );
    }

    /**
     * Captura excepciones de recurso no encontrado (entidad inexistente en la BD).
     * Devuelve HTTP 404 (NOT_FOUND) con los detalles del error.
     * Esta excepción se usa, por ejemplo, cuando un cliente intenta dejar una reseña
     * sin tener una visita completada para esa propiedad.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ExceptionMessage resourceNotFoundException(ResourceNotFoundException e, WebRequest r) {
        return new ExceptionMessage(
                HttpStatus.NOT_FOUND.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                r.getDescription(false),
                LocalDateTime.now()
        );
    }
}
