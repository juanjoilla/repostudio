package com.artesluis.artesluis_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "artesluis-backend");
        response.put("version", "0.0.1-SNAPSHOT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "ArteisLuis Backend API está funcionando correctamente");
        response.put("status", "OK");
        response.put("timestamp", LocalDateTime.now().toString());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("authentication", "/api/usuarios/login");
        endpoints.put("registration", "/api/usuarios/registro");
        endpoints.put("profile", "/api/usuarios/perfil/{id}");
        endpoints.put("roles", "/api/roles");
        endpoints.put("admin", "/api/admin/*");
        endpoints.put("health", "/api/health");
        response.put("endpoints", endpoints);
        
        return ResponseEntity.ok(response);
    }
}