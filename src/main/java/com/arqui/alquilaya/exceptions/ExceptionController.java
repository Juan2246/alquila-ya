package com.arqui.alquilaya.exceptions;

import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionMessage camposInvalidos(MethodArgumentNotValidException e, WebRequest r) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getField() + ": " + campo.getDefaultMessage()).distinct()
                .collect(Collectors.joining("; "));
        return new ExceptionMessage(400, "Validacion", mensaje, r.getDescription(false), LocalDateTime.now());
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionMessage fechaInvalida(WebRequest r) {
        return new ExceptionMessage(400, "Validacion", "Formato de fecha no válido", r.getDescription(false), LocalDateTime.now());
    }

    @ExceptionHandler({DataIntegrityViolationException.class,
            ObjectOptimisticLockingFailureException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionMessage conflicto(WebRequest r) {
        return new ExceptionMessage(409, "Conflicto", "El recurso ya existe, tiene relaciones o fue modificado. Actualiza la consulta.", r.getDescription(false), LocalDateTime.now());
    }


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