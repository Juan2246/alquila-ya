package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.ResenaDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.entities.Resena;
import com.arqui.alquilaya.entities.Reserva;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.ResenaRepository;
import com.arqui.alquilaya.repositories.ReservaRepository;
import com.arqui.alquilaya.security.AccesoActual;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.PropiedadService;
import com.arqui.alquilaya.services.ResenaService;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de reseñas.
 * CONTIENE LA REGLA DE NEGOCIO CRÍTICA: antes de permitir que un cliente deje una reseña,
 * se valida que tenga al menos una RESERVA con estado "COMPLETADA" para esa propiedad.
 * La puntuación general se calcula automáticamente como el promedio de las 3 subcategorías.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;
    private final AccesoActual acceso;

    // Se inyecta ReservaRepository para la validación de reserva completada
    private final ReservaRepository reservaRepository;

    private final ClienteService clienteService;

    private final PropiedadService propiedadService;

    @Override
    public Resena responder(Long id, String respuesta) {
        Resena resena = resenaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));
        acceso.exigirPropietario(resena.getPropiedad().getPropietario());
        if (respuesta == null || respuesta.isBlank() || respuesta.length() > 2000)
            throw new ValidationException("La respuesta debe tener entre 1 y 2000 caracteres");
        resena.setRespuestaPropietario(respuesta.trim());
        return resenaRepository.save(resena);
    }

    @Override
    public Resena findById(Long id) {
        return resenaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Resena> listByPropiedadId(Long propiedadId) {
        return resenaRepository.findByPropiedad_Id(propiedadId);
    }

    /**
     * Crea una nueva reseña con puntuaciones detalladas.
     *
     * REGLA: El cliente DEBE tener una Reserva registrada y COMPLETADA para esa Propiedad.
     *
     * Flujo:
     * 1. Buscar al cliente y la propiedad por sus IDs.
     * 2. VALIDAR que exista al menos una reserva con estado "COMPLETADA" para ese cliente + propiedad.
     * 3. Si no hay reserva completada → lanzar ResourceNotFoundException.
     * 4. Validar que las 3 subcategorías estén presentes y sean válidas (1-5).
     * 5. Calcular puntuación general como promedio de las 3 subcategorías.
     * 6. Crear la reseña y guardarla.
     */
    @Override
    public ResenaDTO addDTO(ResenaDTO resenaDTO) {
        // Paso 1: Buscar el cliente
        Cliente cliente = clienteService.findById(resenaDTO.getClienteId());
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id: " + resenaDTO.getClienteId() + " no encontrado");
        }

        acceso.exigirCliente(cliente);
        // Paso 2: Buscar la propiedad
        Propiedad propiedad = propiedadService.findById(resenaDTO.getPropiedadId());

        // ============================================================
        // PASO 3 - REGLA DE NEGOCIO CRÍTICA:
        // Verificar que el cliente tenga una RESERVA COMPLETADA para esta propiedad.
        // Se usa el query method findByCliente_IdAndPropiedad_IdAndEstado del ReservaRepository.
        // ============================================================
        List<Reserva> reservasCompletadas = reservaRepository
                .findByCliente_IdAndPropiedad_IdAndEstado(
                        resenaDTO.getClienteId(),
                        resenaDTO.getPropiedadId(),
                        "COMPLETADA"
                );

        if (reservasCompletadas.isEmpty()) {
            throw new ResourceNotFoundException(
                    "El cliente con id: " + resenaDTO.getClienteId()
                    + " no tiene una reserva completada para la propiedad con id: "
                    + resenaDTO.getPropiedadId()
                    + ". No se puede crear la reseña sin una reserva completada."
            );
        }

        // Paso 4: Validar subcategorías
        Integer limpieza = resenaDTO.getPuntuacionLimpieza();
        Integer ubicacion = resenaDTO.getPuntuacionUbicacion();
        Integer comunicacion = resenaDTO.getPuntuacionComunicacion();

        if (limpieza == null || ubicacion == null || comunicacion == null) {
            throw new ValidationException(
                    "Las puntuaciones de limpieza, ubicación y comunicación son obligatorias"
            );
        }
        if (limpieza < 1 || limpieza > 5 || ubicacion < 1 || ubicacion > 5 || comunicacion < 1 || comunicacion > 5) {
            throw new ValidationException(
                    "Las puntuaciones deben estar entre 1 y 5"
            );
        }

        // Paso 5: Calcular puntuación general como promedio redondeado
        int puntuacionGeneral = Math.round((limpieza + ubicacion + comunicacion) / 3.0f);

        // Paso 6: Crear y guardar la reseña
        Resena newResena = new Resena(
                null,
                puntuacionGeneral,
                limpieza,
                ubicacion,
                comunicacion,
                resenaDTO.getComentario(),
                LocalDateTime.now(),
                propiedad,
                cliente, null
        );

        newResena = resenaRepository.save(newResena);

        resenaDTO.setId(newResena.getId());
        resenaDTO.setPuntuacion(puntuacionGeneral);
        resenaDTO.setClienteNombre(cliente.getNombre() + " " + cliente.getApellido());
        resenaDTO.setPropiedadTitulo(propiedad.getTitulo());
        return resenaDTO;
    }
}