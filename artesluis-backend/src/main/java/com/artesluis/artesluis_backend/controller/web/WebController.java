package com.artesluis.artesluis_backend.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/planes")
    public String planes(Model model) {
        return "planes";
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
}