package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> listarUsuarios();

    Usuario obtenerUsuarioPorId(Long id);

    Usuario guardarUsuario(Usuario usuario);

    void eliminarUsuario(Long id);

    Usuario obtenerPorCorreo(String correo);
}
