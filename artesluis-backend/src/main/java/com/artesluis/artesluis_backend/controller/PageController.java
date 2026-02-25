package com.artesluis.artesluis_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para las páginas estáticas del sitio web.
 * El UserSessionInterceptor se encarga de agregar automáticamente
 * la información del usuario y carrito a todas las vistas.
 */
@Controller
public class PageController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }
    
    @GetMapping("/mision")
    public String mision() {
        return "mision";
    }
    
    @GetMapping("/portafolio")
    public String portafolio() {
        return "portafolio";
    }
    
    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }
    
    @GetMapping("/enlace")
    public String enlace() {
        return "enlace";
    }
}
