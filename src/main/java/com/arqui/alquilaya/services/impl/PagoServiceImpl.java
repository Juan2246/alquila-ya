package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.PagoDTO;
import com.arqui.alquilaya.entities.Contrato;
import com.arqui.alquilaya.entities.Pago;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.PagoRepository;
import com.arqui.alquilaya.security.AccesoActual;
import com.arqui.alquilaya.services.ContratoService;
import com.arqui.alquilaya.services.PagoService;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de pagos.
 * Registra pagos de alquiler asociados a un contrato.
 * Utiliza BigDecimal para el monto, garantizando precisión monetaria.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ContratoService contratoService;
    private final AccesoActual acceso;

    @Override
    public Pago findById(Long id) {
        Pago pago = pagoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        acceso.exigirParticipante(pago.getContrato());
        return pago;
    }

    @Override
    public List<Pago> listByContratoId(Long contratoId) {
        contratoService.findById(contratoId);
        return pagoRepository.findByContrato_Id(contratoId);
    }

    @Override
    public PagoDTO addDTO(PagoDTO pagoDTO) {
        Contrato contrato = contratoService.findById(pagoDTO.getContratoId());
        if (contrato == null) {
            throw new ResourceNotFoundException("Contrato con id: " + pagoDTO.getContratoId() + " no encontrado");
        }

        acceso.exigirPropietario(contrato.getPropiedad().getPropietario());
        if (!"FIRMADO".equals(contrato.getEstado()) || pagoDTO.getMonto() == null || pagoDTO.getMonto().signum() <= 0
                || pagoDTO.getMonto().scale() > 2 || pagoDTO.getMetodo() == null || pagoDTO.getMetodo().isBlank())
            throw new ValidationException("Registra un importe positivo, método y contrato firmado");
        BigDecimal pagado = pagoRepository.findByContrato_Id(contrato.getId()).stream()
                .map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (contrato.getReserva() != null && pagado.add(pagoDTO.getMonto()).compareTo(contrato.getReserva().getPrecioTotal()) > 0)
            throw new ValidationException("El importe supera el saldo del contrato");
        LocalDateTime fecha = LocalDateTime.now();
        pagoDTO.setFecha(fecha.toString()); pagoDTO.setEstado("COMPLETADO");

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