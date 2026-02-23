package com.artesluis.artesluis_backend.security;

import com.artesluis.artesluis_backend.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler para manejar el éxito de autenticación y establecer atributos de sesión
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Obtener usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();
        
        // Establecer atributos de sesión para compatibilidad con código existente
        HttpSession session = request.getSession();
        session.setAttribute("usuario", usuario);
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("rolUsuario", usuario.getRol().getNombre());
        
        // Redirigir según el rol
        String targetUrl = determinarRedirect(usuario.getRol().getNombre());
        response.sendRedirect(targetUrl);
    }
    
    private String determinarRedirect(String rol) {
        switch (rol) {
            case "ADMIN":
                return "/admin";
            case "ARTISTA":
                return "/admin";
            case "MODERADOR":
                return "/admin";
            case "CLIENTE":
            default:
                return "/planes";
        }
    }
}
