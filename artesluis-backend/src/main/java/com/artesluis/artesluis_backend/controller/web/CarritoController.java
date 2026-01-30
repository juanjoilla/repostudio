package com.artesluis.artesluis_backend.controller.web;

import com.artesluis.artesluis_backend.model.Carrito;
import com.artesluis.artesluis_backend.model.Usuario;
import com.artesluis.artesluis_backend.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @Autowired
    private CarritoService carritoService;
    
    /**
     * Muestra la página del carrito de compras
     */
    @GetMapping
    public String mostrarCarrito(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        // Verificar si el usuario está logueado
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        
        String sessionId = session.getId();
        
        // Obtener el carrito
        Carrito carrito = carritoService.obtenerCarritoPorSession(sessionId);
        
        // Agregar datos al modelo
        model.addAttribute("carrito", carrito);
        model.addAttribute("cantidadItems", carrito.contarItems());
        model.addAttribute("total", carrito.calcularTotal());
        model.addAttribute("usuario", usuarioLogueado);
        model.addAttribute("usuarioLogueado", true);
        
        return "carrito";
    }
}