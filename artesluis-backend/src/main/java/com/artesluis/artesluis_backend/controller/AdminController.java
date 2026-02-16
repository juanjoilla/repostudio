package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.*;
import com.artesluis.artesluis_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private OrdenService ordenService;
    
    @Autowired
    private PagoService pagoService;

    // Cargar usuarios automáticamente usando sesión
    @GetMapping("/usuarios/auto")
    public ResponseEntity<List<Map<String, Object>>> listarUsuariosAuto() {
        try {
            List<Usuario> usuarios = usuarioService.listarUsuarios();
            List<Map<String, Object>> usuariosSeguros = usuarios.stream()
                .map(u -> {
                    Map<String, Object> usuarioMap = new HashMap<>();
                    usuarioMap.put("id", u.getId());
                    usuarioMap.put("nombre", u.getNombre());
                    usuarioMap.put("correo", u.getCorreo());
                    usuarioMap.put("imagenUrl", u.getImagenUrl() != null ? u.getImagenUrl() : "");
                    usuarioMap.put("rol", u.getRol().getNombre());
                    return usuarioMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(usuariosSeguros);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // Listar todos los usuarios - SOLO PARA ADMINISTRADORES
    @PostMapping("/usuarios")
    public ResponseEntity<Map<String, Object>> listarUsuarios(@RequestBody Map<String, String> adminCredentials) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar que quien hace la petición es administrador
            String adminEmail = adminCredentials.get("adminEmail");
            String adminPassword = adminCredentials.get("adminPassword");
            
            if (adminEmail == null || adminPassword == null) {
                response.put("success", false);
                response.put("message", "Credenciales de administrador requeridas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Usuario admin = usuarioService.obtenerPorCorreo(adminEmail);
            
            if (admin == null || !admin.getPassword().equals(adminPassword) || 
                !admin.getRol().getNombre().equals("ADMIN")) {
                response.put("success", false);
                response.put("message", "Acceso denegado - Se requieren privilegios de administrador");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Si es admin, retornar lista de usuarios (sin contraseñas)
            List<Usuario> usuarios = usuarioService.listarUsuarios();
            List<Map<String, Object>> usuariosSeguros = usuarios.stream()
                .map(u -> {
                    Map<String, Object> usuarioMap = new HashMap<>();
                    usuarioMap.put("id", u.getId());
                    usuarioMap.put("nombre", u.getNombre());
                    usuarioMap.put("correo", u.getCorreo());
                    usuarioMap.put("imagenUrl", u.getImagenUrl() != null ? u.getImagenUrl() : "");
                    usuarioMap.put("rol", u.getRol().getNombre());
                    return usuarioMap;
                })
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("usuarios", usuariosSeguros);
            response.put("total", usuariosSeguros.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Eliminar usuario - SOLO PARA ADMINISTRADORES
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(
            @PathVariable Long id, 
            @RequestBody Map<String, String> adminCredentials) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar credenciales de administrador
            String adminEmail = adminCredentials.get("adminEmail");
            String adminPassword = adminCredentials.get("adminPassword");
            
            if (adminEmail == null || adminPassword == null) {
                response.put("success", false);
                response.put("message", "Credenciales de administrador requeridas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Usuario admin = usuarioService.obtenerPorCorreo(adminEmail);
            
            if (admin == null || !admin.getPassword().equals(adminPassword) || 
                !admin.getRol().getNombre().equals("ADMIN")) {
                response.put("success", false);
                response.put("message", "Acceso denegado - Se requieren privilegios de administrador");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Verificar que el usuario a eliminar existe
            Usuario usuarioAEliminar = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioAEliminar == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            // No permitir que el admin se elimine a sí mismo
            if (usuarioAEliminar.getId().equals(admin.getId())) {
                response.put("success", false);
                response.put("message", "No puedes eliminar tu propia cuenta");
                return ResponseEntity.badRequest().body(response);
            }
            
            usuarioService.eliminarUsuario(id);
            
            response.put("success", true);
            response.put("message", "Usuario eliminado exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Buscar usuario por correo - SOLO PARA ADMINISTRADORES
    @PostMapping("/usuarios/buscar")
    public ResponseEntity<Map<String, Object>> buscarUsuario(@RequestBody Map<String, String> busqueda) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminEmail = busqueda.get("adminEmail");
            String adminPassword = busqueda.get("adminPassword");
            String correoABuscar = busqueda.get("correo");
            
            if (adminEmail == null || adminPassword == null || correoABuscar == null) {
                response.put("success", false);
                response.put("message", "Credenciales de administrador y correo a buscar son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Usuario admin = usuarioService.obtenerPorCorreo(adminEmail);
            
            if (admin == null || !admin.getPassword().equals(adminPassword) || 
                !admin.getRol().getNombre().equals("ADMIN")) {
                response.put("success", false);
                response.put("message", "Acceso denegado - Se requieren privilegios de administrador");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            Usuario usuario = usuarioService.obtenerPorCorreo(correoABuscar);
            
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            response.put("success", true);
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("id", usuario.getId());
            usuarioMap.put("nombre", usuario.getNombre());
            usuarioMap.put("correo", usuario.getCorreo());
            usuarioMap.put("imagenUrl", usuario.getImagenUrl() != null ? usuario.getImagenUrl() : "");
            usuarioMap.put("rol", usuario.getRol().getNombre());
            response.put("usuario", usuarioMap);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}