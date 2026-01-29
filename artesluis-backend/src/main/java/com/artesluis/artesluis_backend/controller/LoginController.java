package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.Usuario;
import com.artesluis.artesluis_backend.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String correo,
            @RequestParam String password,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Buscar usuario por correo y verificar password
            Usuario usuario = usuarioService.obtenerPorCorreo(correo);
            
            if (usuario != null && usuario.getPassword().equals(password)) {
                // Login exitoso
                session.setAttribute("usuario", usuario);
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("rolUsuario", usuario.getRol().getNombre());
                
                response.put("success", true);
                response.put("message", "Inicio de sesión exitoso");
                response.put("redirectUrl", determinarRedirect(usuario.getRol().getNombre()));
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Correo o contraseña incorrectos");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private String determinarRedirect(String rol) {
        switch (rol) {
            case "ADMIN":
                return "/admin";
            case "ARTISTA":
                return "/admin"; // Los artistas también van al panel
            case "MODERADOR":
                return "/admin";
            case "CLIENTE":
            default:
                return "/planes"; // Los clientes van a ver planes
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    // Método simple para simular login (solo para pruebas)
    @GetMapping("/demo-login")
    public String demoLogin(@RequestParam(defaultValue = "maria@ejemplo.com") String correo, 
                           HttpSession session) {
        try {
            Usuario usuario = usuarioService.obtenerPorCorreo(correo);
            if (usuario != null) {
                session.setAttribute("usuario", usuario);
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("rolUsuario", usuario.getRol().getNombre());
                
                return "redirect:" + determinarRedirect(usuario.getRol().getNombre());
            }
        } catch (Exception e) {
            // Si no se encuentra el usuario, crear uno temporal
            session.setAttribute("usuario", correo);
            session.setAttribute("rolUsuario", "CLIENTE");
        }
        
        return "redirect:/planes";
    }
}