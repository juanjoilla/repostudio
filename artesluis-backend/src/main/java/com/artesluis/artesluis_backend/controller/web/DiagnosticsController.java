package com.artesluis.artesluis_backend.controller.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticsController {

    @GetMapping("/resources")
    public ResponseEntity<Map<String, Object>> checkResources() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Boolean> resourceStatus = new HashMap<>();
        
        // Lista de recursos críticos a verificar
        String[] criticalResources = {
            "static/img/video.mp4",
            "static/img/logo-artesluis.png", 
            "static/img/fondo.png",
            "static/img/correo.png",
            "static/img/diseño_001.jpg",
            "static/img/diseño_002.jpg",
            "static/img/diseño_003.jpg",
            "static/img/fondo-pinceladas.jpg",
            "static/css/styles.css",
            "static/css/login.css",
            "static/js/artesluis.js"
        };
        
        int existingCount = 0;
        
        for (String resourcePath : criticalResources) {
            Resource resource = new ClassPathResource(resourcePath);
            boolean exists = resource.exists();
            resourceStatus.put(resourcePath, exists);
            if (exists) existingCount++;
        }
        
        result.put("totalResources", criticalResources.length);
        result.put("existingResources", existingCount);
        result.put("missingResources", criticalResources.length - existingCount);
        result.put("resourceStatus", resourceStatus);
        result.put("healthCheck", existingCount == criticalResources.length ? "HEALTHY" : "ISSUES_FOUND");
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/images")
    public ResponseEntity<Map<String, Object>> checkImages() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Boolean> imageStatus = new HashMap<>();
        
        // Lista de imágenes comúnmente usadas
        String[] imageNames = {
            "video.mp4", "logo-artesluis.png", "fondo.png", "correo.png",
            "diseño_001.jpg", "diseño_002.jpg", "diseño_003.jpg", "diseño_004.jpg",
            "diseño_005.jpg", "fondo-pinceladas.jpg", "logo-facebook.png", 
            "logo-whatsapp.png", "logo-misión.png"
        };
        
        int availableImages = 0;
        
        for (String imageName : imageNames) {
            Resource resource = new ClassPathResource("static/img/" + imageName);
            boolean exists = resource.exists();
            imageStatus.put(imageName, exists);
            if (exists) availableImages++;
        }
        
        result.put("totalChecked", imageNames.length);
        result.put("availableImages", availableImages);
        result.put("imageStatus", imageStatus);
        
        return ResponseEntity.ok(result);
    }
}