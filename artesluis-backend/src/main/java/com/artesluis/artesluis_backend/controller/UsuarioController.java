package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.Usuario;
import com.artesluis.artesluis_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint de autenticación - LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> autenticar(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String correo = credentials.get("correo");
            String password = credentials.get("password");
            
            if (correo == null || password == null) {
                response.put("success", false);
                response.put("message", "Correo y contraseña son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Usuario usuario = usuarioService.obtenerPorCorreo(correo);
            
            if (usuario == null || !usuario.getPassword().equals(password)) {
                response.put("success", false);
                response.put("message", "Credenciales inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Login exitoso - no retornar la contraseña
            response.put("success", true);
            response.put("message", "Autenticación exitosa");
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("id", usuario.getId());
            usuarioMap.put("nombre", usuario.getNombre());
            usuarioMap.put("correo", usuario.getCorreo());
            usuarioMap.put("imagenUrl", usuario.getImagenUrl());
            usuarioMap.put("rol", usuario.getRol().getNombre());
            response.put("usuario", usuarioMap);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Crear nuevo usuario - REGISTRO
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> crearUsuario(@RequestBody Usuario usuario) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (usuario.getCorreo() == null || usuario.getPassword() == null || usuario.getNombre() == null) {
                response.put("success", false);
                response.put("message", "Nombre, correo y contraseña son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Verificar si el usuario ya existe
            Usuario usuarioExistente = usuarioService.obtenerPorCorreo(usuario.getCorreo());
            if (usuarioExistente != null) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario con este correo");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
            
            response.put("success", true);
            response.put("message", "Usuario creado exitosamente");
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("id", nuevoUsuario.getId());
            usuarioMap.put("nombre", nuevoUsuario.getNombre());
            usuarioMap.put("correo", nuevoUsuario.getCorreo());
            usuarioMap.put("rol", nuevoUsuario.getRol().getNombre());
            response.put("usuario", usuarioMap);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener perfil del usuario autenticado (requiere ID)
    @GetMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPerfil(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            
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
            response.put("message", "Error al obtener perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Actualizar perfil del usuario
    @PutMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            // Actualizar solo campos permitidos
            if (datos.containsKey("nombre")) {
                usuario.setNombre(datos.get("nombre"));
            }
            if (datos.containsKey("imagenUrl")) {
                usuario.setImagenUrl(datos.get("imagenUrl"));
            }
            
            Usuario usuarioActualizado = usuarioService.guardarUsuario(usuario);
            
            response.put("success", true);
            response.put("message", "Perfil actualizado exitosamente");
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("id", usuarioActualizado.getId());
            usuarioMap.put("nombre", usuarioActualizado.getNombre());
            usuarioMap.put("correo", usuarioActualizado.getCorreo());
            usuarioMap.put("imagenUrl", usuarioActualizado.getImagenUrl() != null ? usuarioActualizado.getImagenUrl() : "");
            usuarioMap.put("rol", usuarioActualizado.getRol().getNombre());
            response.put("usuario", usuarioMap);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Cambiar contraseña
    @PutMapping("/cambiar-password/{id}")
    public ResponseEntity<Map<String, Object>> cambiarPassword(@PathVariable Long id, @RequestBody Map<String, String> passwords) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String passwordActual = passwords.get("passwordActual");
            String passwordNueva = passwords.get("passwordNueva");
            
            if (passwordActual == null || passwordNueva == null) {
                response.put("success", false);
                response.put("message", "Contraseña actual y nueva son requeridas");
                return ResponseEntity.badRequest().body(response);
            }
            
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            if (!usuario.getPassword().equals(passwordActual)) {
                response.put("success", false);
                response.put("message", "Contraseña actual incorrecta");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            usuario.setPassword(passwordNueva);
            usuarioService.guardarUsuario(usuario);
            
            response.put("success", true);
            response.put("message", "Contraseña cambiada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar contraseña: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}