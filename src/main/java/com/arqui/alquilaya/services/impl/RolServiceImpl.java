package com.arqui.alquilaya.services.impl;

import com.arqui.alquilaya.entities.Rol;
import com.arqui.alquilaya.repositories.RolRepository;
import com.arqui.alquilaya.services.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de roles.
 * Actúa como puente entre el controlador y el repositorio para operaciones con roles.
 * Equivale a AuthorityServiceImpl en el proyecto del profesor.
 */
@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    public Rol findById(Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    @Override
    public Rol findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    @Override
    public Rol add(Rol rol) {
        return rolRepository.save(rol);
    }
}
