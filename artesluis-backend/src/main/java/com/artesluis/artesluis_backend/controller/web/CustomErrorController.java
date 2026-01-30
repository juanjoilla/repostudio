package com.artesluis.artesluis_backend.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object message = request.getAttribute("javax.servlet.error.message");
        Object exception = request.getAttribute("javax.servlet.error.exception");
        Object requestUri = request.getAttribute("javax.servlet.error.request_uri");

        // Log error details for debugging
        logger.warn("Error occurred: Status={}, URI={}, Message={}", status, requestUri, message);

        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);
            
            switch (statusCode) {
                case 404:
                    model.addAttribute("message", "La página que buscas no existe");
                    break;
                case 500:
                    model.addAttribute("message", "Error interno del servidor");
                    break;
                case 403:
                    model.addAttribute("message", "No tienes permisos para acceder a esta página");
                    break;
                default:
                    model.addAttribute("message", "Ha ocurrido un error inesperado (Código " + statusCode + ")");
            }
        } else {
            model.addAttribute("status", "Desconocido");
            model.addAttribute("message", "Ha ocurrido un error inesperado");
        }
        
        // Agregar información de usuario logueado para navegación consistente
        model.addAttribute("usuarioLogueado", request.getSession().getAttribute("usuario") != null);

        return "error";
    }
}