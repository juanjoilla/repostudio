package com.artesluis.artesluis_backend.config;

import com.artesluis.artesluis_backend.model.Usuario;
import com.artesluis.artesluis_backend.model.Carrito;
import com.artesluis.artesluis_backend.service.CarritoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor que agrega automáticamente información del usuario y carrito
 * a todas las vistas cuando hay una sesión activa.
 */
@Component
public class UserSessionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private CarritoService carritoService;
    
    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {
        
        // Solo procesar si es una vista (no una respuesta REST/JSON)
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                
                if (usuario != null) {
                    // Agregar información del usuario a todas las vistas
                    modelAndView.addObject("usuarioLogueado", true);
                    modelAndView.addObject("usuario", usuario);
                    modelAndView.addObject("usuarioId", usuario.getId());
                    modelAndView.addObject("rolUsuario", usuario.getRol().getNombre());
                    
                    // Agregar información del carrito
                    try {
                        String sessionId = session.getId();
                        Carrito carrito = carritoService.obtenerCarritoPorSession(sessionId);
                        int cantidadItems = carrito.getItems().stream()
                                .mapToInt(item -> item.getCantidad())
                                .sum();
                        
                        modelAndView.addObject("itemsEnCarrito", cantidadItems);
                    } catch (Exception e) {
                        // Si hay error al obtener el carrito, solo loguear y continuar
                        System.err.println("Error al obtener carrito en interceptor: " + e.getMessage());
                        modelAndView.addObject("itemsEnCarrito", 0);
                    }
                } else {
                    // Usuario no autenticado
                    modelAndView.addObject("usuarioLogueado", false);
                    modelAndView.addObject("itemsEnCarrito", 0);
                }
            } else {
                // Sin sesión
                modelAndView.addObject("usuarioLogueado", false);
                modelAndView.addObject("itemsEnCarrito", 0);
            }
        }
    }
}
