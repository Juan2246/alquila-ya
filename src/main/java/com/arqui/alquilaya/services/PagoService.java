package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.PagoDTO;
import com.arqui.alquilaya.entities.Pago;
import java.util.List;

public interface PagoService {
    public Pago findById(Long id);
    public List<Pago> listByContratoId(Long contratoId);
    public PagoDTO addDTO(PagoDTO pagoDTO);
}
