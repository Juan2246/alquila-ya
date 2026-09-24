package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.PagoDTO;
import com.arqui.alquilaya.entities.Contrato;
import com.arqui.alquilaya.entities.Pago;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.PagoRepository;
import com.arqui.alquilaya.services.ContratoService;
import com.arqui.alquilaya.services.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de pagos.
 * Registra pagos de alquiler asociados a un contrato.
 * Utiliza BigDecimal para el monto, garantizando precisión monetaria.
 */
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ContratoService contratoService;

    @Override
    public Pago findById(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Pago> listByContratoId(Long contratoId) {
        return pagoRepository.findByContrato_Id(contratoId);
    }

    @Override
    public PagoDTO addDTO(PagoDTO pagoDTO) {
        Contrato contrato = contratoService.findById(pagoDTO.getContratoId());
        if (contrato == null) {
            throw new ResourceNotFoundException("Contrato con id: " + pagoDTO.getContratoId() + " no encontrado");
        }

        LocalDateTime fecha = (pagoDTO.getFecha() != null && !pagoDTO.getFecha().isBlank())
                ? LocalDateTime.parse(pagoDTO.getFecha())
                : LocalDateTime.now();

        Pago newPago = new Pago(
                null, pagoDTO.getMonto(), fecha,
                pagoDTO.getEstado() != null ? pagoDTO.getEstado() : "COMPLETADO",
                pagoDTO.getMetodo(), contrato
        );
        newPago = pagoRepository.save(newPago);
        pagoDTO.setId(newPago.getId());
        return pagoDTO;
    }
}
