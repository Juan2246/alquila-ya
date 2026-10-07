package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.ContratoDTO;
import com.arqui.alquilaya.entities.Contrato;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
public interface ContratoService {
    List<Contrato> listByPropiedadId(Long id);
    Resource leerFirma(Long id);
    public Contrato findById(Long id);
    public List<Contrato> listAll();
    public List<Contrato> listByClienteId(Long clienteId);
    public ContratoDTO addDTO(ContratoDTO contratoDTO);
    public Contrato update(Contrato contrato);

    /**
     * Adjunta la imagen de firma a un contrato existente y lo marca como FIRMADO.
     * @param id ID del contrato a firmar
     * @param file imagen de la firma
     * @return el contrato actualizado con la firma adjunta
     */
    public Contrato firmar(Long id, MultipartFile file);
}