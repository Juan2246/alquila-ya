package com.arqui.alquilaya.serviceimpl;

import com.arqui.alquilaya.entities.Rol;
import com.arqui.alquilaya.repositories.RolRepository;
import com.arqui.alquilaya.services.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de roles.
 * Actúa como puente entre el controlador y el repositorio para operaciones con roles.
 * Equivale a AuthorityServiceImpl en el proyecto del profesor.
 */
@Service
public class RolServiceImpl implements RolService {

    @Autowired
    RolRepository rolRepository;

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
