package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.ContratoDTO;
import com.arqui.alquilaya.entities.Contrato;
import java.util.List;

public interface ContratoService {
    public Contrato findById(Long id);
    public List<Contrato> listAll();
    public List<Contrato> listByClienteId(Long clienteId);
    public ContratoDTO addDTO(ContratoDTO contratoDTO);
    public Contrato update(Contrato contrato);
}
