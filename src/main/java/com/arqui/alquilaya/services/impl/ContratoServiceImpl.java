package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.ContratoDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Contrato;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.ContratoRepository;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.ContratoService;
import com.arqui.alquilaya.services.FileStorageService;
import com.arqui.alquilaya.services.PropiedadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de contratos.
 * Gestiona la creación y actualización de contratos de alquiler.
 * Soporta el campo firmaImagenUrl para adjuntar firma visual.
 */
@Service
@RequiredArgsConstructor
public class ContratoServiceImpl implements ContratoService {

    private final ContratoRepository contratoRepository;
    private final ClienteService clienteService;
    private final PropiedadService propiedadService;
    private final FileStorageService fileStorageService;

    @Override
    public Contrato findById(Long id) {
        return contratoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Contrato> listAll() {
        return contratoRepository.findAll();
    }

    @Override
    public List<Contrato> listByClienteId(Long clienteId) {
        return contratoRepository.findByCliente_Id(clienteId);
    }

    @Override
    public ContratoDTO addDTO(ContratoDTO contratoDTO) {
        Cliente cliente = clienteService.findById(contratoDTO.getClienteId());
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id: " + contratoDTO.getClienteId() + " no encontrado");
        }
        Propiedad propiedad = propiedadService.findById(contratoDTO.getPropiedadId());

        LocalDateTime fechaInicio = LocalDateTime.parse(contratoDTO.getFechaInicio());
        LocalDateTime fechaFin = LocalDateTime.parse(contratoDTO.getFechaFin());

        Contrato newContrato = new Contrato(
                null, fechaInicio, fechaFin,
                contratoDTO.getPdf(),
                contratoDTO.getEstado() != null ? contratoDTO.getEstado() : "ACTIVO",
                contratoDTO.getFirmaImagenUrl(),
                cliente, propiedad, null
        );
        newContrato = contratoRepository.save(newContrato);

        contratoDTO.setId(newContrato.getId());
        contratoDTO.setClienteNombre(cliente.getNombre() + " " + cliente.getApellido());
        contratoDTO.setPropiedadTitulo(propiedad.getTitulo());
        return contratoDTO;
    }

    @Override
    public Contrato update(Contrato contrato) {
        Contrato found = findById(contrato.getId());
        if (found == null) {
            throw new ResourceNotFoundException("Contrato con id: " + contrato.getId() + " no encontrado");
        }
        if (contrato.getEstado() != null) found.setEstado(contrato.getEstado());
        if (contrato.getFechaFin() != null) found.setFechaFin(contrato.getFechaFin());
        if (contrato.getPdf() != null) found.setPdf(contrato.getPdf());
        if (contrato.getFirmaImagenUrl() != null) found.setFirmaImagenUrl(contrato.getFirmaImagenUrl());
        return contratoRepository.save(found);
    }

    /**
     * Sube la imagen de firma con el FileStorageService y actualiza el contrato:
     * guarda la ruta en firmaImagenUrl y cambia su estado a FIRMADO.
     */
    @Override
    public Contrato firmar(Long id, MultipartFile file) {
        Contrato contrato = findById(id);
        if (contrato == null) {
            throw new ResourceNotFoundException("Contrato con id: " + id + " no encontrado");
        }

        // Subir la imagen de firma
        String rutaFirma = fileStorageService.guardarArchivo(file, "firmas");

        // Actualizar el contrato con la ruta de la firma
        contrato.setFirmaImagenUrl(rutaFirma);
        contrato.setEstado("FIRMADO");
        return update(contrato);
    }
}
