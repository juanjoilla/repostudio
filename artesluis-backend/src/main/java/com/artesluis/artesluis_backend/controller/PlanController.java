package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.Carrito;
import com.artesluis.artesluis_backend.model.Plan;
import com.artesluis.artesluis_backend.service.CarritoService;
import com.artesluis.artesluis_backend.service.PlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/planes")
public class PlanController {
    
    @Autowired
    private PlanService planService;
    
    @Autowired
    private CarritoService carritoService;
    
    @GetMapping
    public String mostrarPlanes(Model model, HttpSession session) {
        List<Plan> planes = planService.obtenerPlanesOrdenadosPorRecomendacion();
        model.addAttribute("planes", planes);
        
        // Verificar si el usuario está logueado
        boolean usuarioLogueado = session.getAttribute("usuario") != null;
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        
        // Contar items en el carrito para mostrar en la interfaz (solo si está logueado)
        if (usuarioLogueado) {
            String sessionId = session.getId();
            int itemsEnCarrito = carritoService.contarItemsEnCarrito(sessionId);
            model.addAttribute("itemsEnCarrito", itemsEnCarrito);
        } else {
            model.addAttribute("itemsEnCarrito", 0);
        }
        
        return "planes";
    }
    
    @PostMapping("/agregar-carrito")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(
            @RequestParam Long planId,
            @RequestParam(defaultValue = "1") Integer cantidad,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Verificar si el usuario está logueado
        if (session.getAttribute("usuario") == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión para agregar items al carrito");
            response.put("requireLogin", true);
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            String sessionId = session.getId();
            carritoService.agregarPlanAlCarrito(sessionId, planId, cantidad);
            
            int itemsEnCarrito = carritoService.contarItemsEnCarrito(sessionId);
            
            response.put("success", true);
            response.put("message", "Plan agregado al carrito exitosamente");
            response.put("itemsEnCarrito", itemsEnCarrito);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al agregar el plan al carrito: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/carrito")
    public String mostrarCarrito(Model model, HttpSession session) {
        // Verificar si el usuario está logueado
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        String sessionId = session.getId();
        Carrito carrito = carritoService.obtenerCarritoPorSession(sessionId);
        
        model.addAttribute("carrito", carrito);
        model.addAttribute("itemsEnCarrito", carrito.contarItems());
        model.addAttribute("usuarioLogueado", true);
        
        return "carrito";
    }
    
    @PostMapping("/carrito/actualizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarCarrito(
            @RequestParam Long planId,
            @RequestParam Integer cantidad,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            carritoService.actualizarCantidadItem(sessionId, planId, cantidad);
            
            Carrito carrito = carritoService.obtenerCarritoPorSession(sessionId);
            
            response.put("success", true);
            response.put("message", "Carrito actualizado exitosamente");
            response.put("itemsEnCarrito", carrito.contarItems());
            response.put("total", carrito.calcularTotal());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar el carrito: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/carrito/remover")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removerDelCarrito(
            @RequestParam Long planId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            carritoService.removerPlanDelCarrito(sessionId, planId);
            
            int itemsEnCarrito = carritoService.contarItemsEnCarrito(sessionId);
            
            response.put("success", true);
            response.put("message", "Plan removido del carrito exitosamente");
            response.put("itemsEnCarrito", itemsEnCarrito);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al remover el plan del carrito: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/carrito/limpiar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> limpiarCarrito(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            carritoService.limpiarCarrito(sessionId);
            
            response.put("success", true);
            response.put("message", "Carrito limpiado exitosamente");
            response.put("itemsEnCarrito", 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al limpiar el carrito: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}