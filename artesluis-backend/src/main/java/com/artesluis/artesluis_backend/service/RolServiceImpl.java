package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Rol;
import com.artesluis.artesluis_backend.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Override
    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public Rol obtenerRolPorId(Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminarRol(Long id) {
        rolRepository.deleteById(id);
    }
}
