package com.arqui.alquilaya.dtos;

import java.time.LocalDate;

/** Calendario sin identificadores de reserva ni información del huésped. */
public record DisponibilidadDTO(LocalDate fechaCheckIn, LocalDate fechaCheckOut, String estado) {}
