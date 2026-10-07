package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.ContratoService;
import com.arqui.alquilaya.services.FileStorageService;
import com.arqui.alquilaya.services.PropiedadService;
import com.arqui.alquilaya.dtos.ContratoDTO;
import com.arqui.alquilaya.entities.Contrato;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.ContratoRepository;
import com.arqui.alquilaya.security.AccesoActual;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratoServiceImpl implements ContratoService {
    private final ContratoRepository contratoRepository;
    private final ClienteService clienteService;
    private final PropiedadService propiedadService;
    private final FileStorageService fileStorageService;
    private final AccesoActual acceso;

    @Override
    public Contrato findById(Long id) {
        Contrato contrato = contratoRepository.bloquearPorId(id).orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado"));
        acceso.exigirParticipante(contrato);
        return contrato;
    }

    @Override
    public List<Contrato> listAll() {
        Long cuenta = acceso.usuario().getUser().getId();
        return contratoRepository.findByCliente_User_IdOrPropiedad_Propietario_User_Id(cuenta, cuenta);
    }

    @Override
    public List<Contrato> listByClienteId(Long id) {
        acceso.exigirCliente(clienteService.findById(id));
        return contratoRepository.findByCliente_Id(id);
    }

    @Override
    public List<Contrato> listByPropiedadId(Long id) {
        acceso.exigirPropietario(propiedadService.findById(id).getPropietario());
        return contratoRepository.findByPropiedad_Id(id);
    }

    /** Los contratos se crean exclusivamente dentro de la transacción de reserva. */
    @Override
    public ContratoDTO addDTO(ContratoDTO entrada) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Crea una reserva: su contrato se genera automáticamente");
    }

    @Override
    public Contrato update(Contrato entrada) {
        findById(entrada.getId());
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El estado se gestiona mediante la reserva y la firma");
    }

    @Override
    public Contrato firmar(Long id, MultipartFile file) {
        Contrato contrato = findById(id);
        acceso.exigirCliente(contrato.getCliente());
        if (!"ACTIVO".equals(contrato.getEstado()) || contrato.getReserva() == null
                || !"CONFIRMADA".equals(contrato.getReserva().getEstado()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se firma un contrato activo de una reserva confirmada");
        contrato.setFirmaImagenUrl(fileStorageService.guardarArchivo(file, "firmas"));
        contrato.setEstado("FIRMADO");
        return contratoRepository.save(contrato);
    }

    @Override
    public Resource leerFirma(Long id) {
        Contrato contrato = findById(id);
        if (contrato.getFirmaImagenUrl() == null) throw new ResourceNotFoundException("El contrato todavía no tiene firma");
        return fileStorageService.leerFirma(contrato.getFirmaImagenUrl());
    }
}