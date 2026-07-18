package com.arqui.alquilaya.repositories;

import com.arqui.alquilaya.entities.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByCliente_Id(Long clienteId);
}
