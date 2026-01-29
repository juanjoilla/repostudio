package com.artesluis.artesluis_backend.controller.web;

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
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        return "nosotros";
    }

    @GetMapping("/portafolio")
    public String portafolio(Model model) {
        return "portafolio";
    }

    @GetMapping("/contacto")
    public String contacto(Model model) {
        return "contacto";
    }

    @GetMapping("/mision")
    public String mision(Model model) {
        return "mision";
    }

    @GetMapping("/enlace")
    public String enlace(Model model) {
        return "enlace";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
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