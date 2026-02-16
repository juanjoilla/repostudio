package com.artesluis.artesluis_backend.controller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", usuario != null);
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", usuario != null);
        return "nosotros";
    }

    @GetMapping("/portafolio")
    public String portafolio(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", usuario != null);
        return "portafolio";
    }

    @GetMapping("/contacto")
    public String contacto(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", usuario != null);
        return "contacto";
    }

    @GetMapping("/mision")
    public String mision(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", usuario != null);
        return "mision";
    }

    @GetMapping("/enlace")
    public String enlace(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", usuario != null);
        return "enlace";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        // Si ya está logueado, redirigir al admin
        if (usuario != null) {
            return "redirect:/admin";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", false);
        return "login";
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        // Si NO está logueado, redirigir al login
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogueado", true);
        return "admin";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        // Redireccionar dashboard a admin para compatibilidad
        return "redirect:/admin";
    }

    // Endpoint para servir imágenes estáticas con manejo de errores
    @GetMapping("/img/{imageName}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Resource resource = new ClassPathResource("static/img/" + imageName);
            
            if (!resource.exists()) {
                // Si no existe, retornar imagen placeholder
                resource = new ClassPathResource("static/img/logo-artesluis.png");
            }
            
            // Determinar el tipo de contenido basado en la extensión
            MediaType mediaType = getMediaTypeForImage(imageName);
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint para verificar recursos
    @GetMapping("/api/check-resources")
    @ResponseBody
    public ResponseEntity<String> checkResources() {
        StringBuilder report = new StringBuilder();
        
        String[] criticalImages = {
            "video.mp4", "logo-artesluis.png", "fondo.png", "correo.png"
        };
        
        for (String imageName : criticalImages) {
            Resource resource = new ClassPathResource("static/img/" + imageName);
            report.append(imageName).append(": ")
                  .append(resource.exists() ? "✓ EXISTS" : "✗ MISSING")
                  .append("\n");
        }
        
        return ResponseEntity.ok(report.toString());
    }

    private MediaType getMediaTypeForImage(String imageName) {
        String extension = imageName.toLowerCase();
        
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (extension.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (extension.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (extension.endsWith(".mp4")) {
            return MediaType.valueOf("video/mp4");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}