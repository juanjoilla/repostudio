package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.Rol;
import com.artesluis.artesluis_backend.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public List<Rol> listarRoles() {
        return rolService.listarRoles();
    }

    @PostMapping
    public Rol crearRol(@RequestBody Rol rol) {
        return rolService.guardarRol(rol);
    }

    @GetMapping("/{id}")
    public Rol obtenerRol(@PathVariable Long id) {
        return rolService.obtenerRolPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarRol(@PathVariable Long id) {
        rolService.eliminarRol(id);
    }
}
