package com.arqui.alquilaya.serviceimpl;

import com.arqui.alquilaya.entities.Propietario;
import com.arqui.alquilaya.repositories.PropietarioRepository;
import com.arqui.alquilaya.services.PropietarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de propietarios.
 * Gestiona las operaciones de perfil de los dueños de inmuebles.
 */
@Service
public class PropietarioServiceImpl implements PropietarioService {

    @Autowired
    PropietarioRepository propietarioRepository;

    @Override
    public Propietario add(Propietario propietario) {
        return propietarioRepository.save(propietario);
    }

    @Override
    public Propietario findById(Long id) {
        return propietarioRepository.findById(id).orElse(null);
    }

    @Override
    public List<Propietario> listAll() {
        return propietarioRepository.findAll();
    }

    @Override
    public Propietario findByUserId(Long userId) {
        return propietarioRepository.findByUser_Id(userId);
    }
}
