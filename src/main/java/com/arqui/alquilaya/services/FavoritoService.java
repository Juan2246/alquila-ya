package com.arqui.alquilaya.services;

import com.arqui.alquilaya.dtos.FavoritoDTO;
import com.arqui.alquilaya.entities.Favorito;
import java.util.List;

public interface FavoritoService {
    public List<Favorito> listByClienteId(Long clienteId);
    public FavoritoDTO addDTO(FavoritoDTO favoritoDTO);
    public void delete(Long id);
}
