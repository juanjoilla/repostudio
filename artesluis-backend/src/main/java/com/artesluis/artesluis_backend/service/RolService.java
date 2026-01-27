package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Rol;
import java.util.List;

public interface RolService {
    List<Rol> listarRoles();

    Rol guardarRol(Rol rol);

    Rol obtenerRolPorId(Long id);

    void eliminarRol(Long id);
}
