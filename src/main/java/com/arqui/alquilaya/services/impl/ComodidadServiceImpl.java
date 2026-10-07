package com.arqui.alquilaya.services.impl;
import com.arqui.alquilaya.entities.Comodidad;
import com.arqui.alquilaya.repositories.ComodidadRepository;
import com.arqui.alquilaya.services.ComodidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComodidadServiceImpl implements ComodidadService {
    private final ComodidadRepository comodidades;
    public List<Comodidad> listAll() { return comodidades.findAll(); }
}
