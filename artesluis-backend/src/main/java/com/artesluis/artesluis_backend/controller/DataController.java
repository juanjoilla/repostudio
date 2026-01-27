package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.Rol;
import com.artesluis.artesluis_backend.model.Usuario;
import com.artesluis.artesluis_backend.service.RolService;
import com.artesluis.artesluis_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/sample")
    public ResponseEntity<Map<String, Object>> cargarDatosPrueba() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Crear roles si no existen
            Rol adminRole = crearRolSiNoExiste("ADMIN");
            Rol clienteRole = crearRolSiNoExiste("CLIENTE");
            Rol artistaRole = crearRolSiNoExiste("ARTISTA");
            Rol moderadorRole = crearRolSiNoExiste("MODERADOR");

            // Crear usuarios de prueba
            int usuariosCreados = 0;

            // Administradores
            usuariosCreados += crearUsuarioSiNoExiste("Luis Artista", "luis@artesluis.com", "admin123", null, adminRole);
            usuariosCreados += crearUsuarioSiNoExiste("Admin Sistema", "admin@artesluis.com", "system456", null, adminRole);

            // Artistas
            usuariosCreados += crearUsuarioSiNoExiste("María González", "maria.gonzalez@email.com", "artista123", "https://example.com/images/maria.jpg", artistaRole);
            usuariosCreados += crearUsuarioSiNoExiste("Carlos Pérez", "carlos.perez@email.com", "carlos789", "https://example.com/images/carlos.jpg", artistaRole);
            usuariosCreados += crearUsuarioSiNoExiste("Ana Rodríguez", "ana.rodriguez@email.com", "ana456", null, artistaRole);

            // Clientes
            usuariosCreados += crearUsuarioSiNoExiste("Juan Cliente", "juan.cliente@email.com", "cliente123", null, clienteRole);
            usuariosCreados += crearUsuarioSiNoExiste("Elena Martínez", "elena.martinez@email.com", "elena456", "https://example.com/images/elena.jpg", clienteRole);
            usuariosCreados += crearUsuarioSiNoExiste("Roberto Silva", "roberto.silva@email.com", "roberto789", null, clienteRole);
            usuariosCreados += crearUsuarioSiNoExiste("Carmen López", "carmen.lopez@email.com", "carmen123", "https://example.com/images/carmen.jpg", clienteRole);
            usuariosCreados += crearUsuarioSiNoExiste("David Fernández", "david.fernandez@email.com", "david456", null, clienteRole);

            // Moderadores
            usuariosCreados += crearUsuarioSiNoExiste("Mónica Moderadora", "monica.mod@artesluis.com", "mod123", "https://example.com/images/monica.jpg", moderadorRole);
            usuariosCreados += crearUsuarioSiNoExiste("Pedro Supervisor", "pedro.supervisor@artesluis.com", "super456", null, moderadorRole);

            response.put("success", true);
            response.put("message", "Datos de prueba cargados exitosamente");
            response.put("usuariosCreados", usuariosCreados);
            response.put("totalUsuarios", usuarioService.listarUsuarios().size());
            response.put("totalRoles", rolService.listarRoles().size());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cargar datos de prueba: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsuarios", usuarioService.listarUsuarios().size());
        stats.put("totalRoles", rolService.listarRoles().size());
        
        return ResponseEntity.ok(stats);
    }

    private Rol crearRolSiNoExiste(String nombreRol) {
        try {
            // Intentar buscar el rol primero
            for (Rol rol : rolService.listarRoles()) {
                if (rol.getNombre().equals(nombreRol)) {
                    return rol;
                }
            }
            // Si no existe, crearlo
            Rol nuevoRol = new Rol(nombreRol);
            return rolService.guardarRol(nuevoRol);
        } catch (Exception e) {
            System.err.println("Error creando rol " + nombreRol + ": " + e.getMessage());
            return null;
        }
    }

    private int crearUsuarioSiNoExiste(String nombre, String correo, String password, String imagenUrl, Rol rol) {
        try {
            // Verificar si el usuario ya existe
            Usuario usuarioExistente = usuarioService.obtenerPorCorreo(correo);
            if (usuarioExistente != null) {
                return 0; // Usuario ya existe, no se crea
            }

            Usuario nuevoUsuario = new Usuario(nombre, correo, password, rol);
            if (imagenUrl != null) {
                nuevoUsuario.setImagenUrl(imagenUrl);
            }
            usuarioService.guardarUsuario(nuevoUsuario);
            return 1; // Usuario creado
        } catch (Exception e) {
            System.err.println("Error creando usuario " + correo + ": " + e.getMessage());
            return 0;
        }
    }
}