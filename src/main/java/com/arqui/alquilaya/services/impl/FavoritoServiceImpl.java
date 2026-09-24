package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.dtos.FavoritoDTO;
import com.arqui.alquilaya.entities.Cliente;
import com.arqui.alquilaya.entities.Favorito;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.exceptions.ResourceNotFoundException;
import com.arqui.alquilaya.repositories.FavoritoRepository;
import com.arqui.alquilaya.services.ClienteService;
import com.arqui.alquilaya.services.FavoritoService;
import com.arqui.alquilaya.services.PropiedadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de favoritos.
 * Permite a los clientes guardar propiedades de su interés.
 */
@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ClienteService clienteService;
    private final PropiedadService propiedadService;

    @Override
    public List<Favorito> listByClienteId(Long clienteId) {
        return favoritoRepository.findByCliente_Id(clienteId);
    }

    @Override
    public FavoritoDTO addDTO(FavoritoDTO favoritoDTO) {
        Cliente cliente = clienteService.findById(favoritoDTO.getClienteId());
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id: " + favoritoDTO.getClienteId() + " no encontrado");
        }
        Propiedad propiedad = propiedadService.findById(favoritoDTO.getPropiedadId());

        Favorito newFavorito = new Favorito(null, LocalDateTime.now(), cliente, propiedad);
        newFavorito = favoritoRepository.save(newFavorito);

        favoritoDTO.setId(newFavorito.getId());
        favoritoDTO.setPropiedadTitulo(propiedad.getTitulo());
        return favoritoDTO;
    }

    @Override
    public void delete(Long id) {
        if (!favoritoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Favorito con id: " + id + " no encontrado");
        }
        favoritoRepository.deleteById(id);
    }
}
