package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.Plan;
import com.artesluis.artesluis_backend.repository.PlanRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminPlanesController {

    @Autowired
    private PlanRepository planRepository;

    // Mostrar la vista de gestión de planes
    @GetMapping("/admin/planes")
    public String gestionPlanes(Model model, HttpSession session) {
        // Verificar que el usuario esté autenticado como admin
        if (session.getAttribute("usuario") == null || 
            !"ADMIN".equals(session.getAttribute("rolUsuario"))) {
            return "redirect:/login";
        }
        
        List<Plan> planes = planRepository.findAll();
        model.addAttribute("planes", planes);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "admin/gestion-planes";
    }

    // API REST para obtener todos los planes
    @GetMapping("/api/planes")
    @ResponseBody
    public ResponseEntity<List<Plan>> listarPlanes() {
        List<Plan> planes = planRepository.findAll();
        return ResponseEntity.ok(planes);
    }

    // API REST para obtener un plan por ID
    @GetMapping("/api/planes/{id}")
    @ResponseBody
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        Optional<Plan> plan = planRepository.findById(id);
        return plan.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // API REST para crear un nuevo plan
    @PostMapping("/api/planes")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan) {
        Plan nuevoPlan = planRepository.save(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlan);
    }

    // API REST para actualizar un plan existente
    @PutMapping("/api/planes/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Plan> actualizarPlan(@PathVariable Long id, 
                                                @RequestBody Plan planActualizado) {
        Optional<Plan> planExistente = planRepository.findById(id);
        if (planExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Plan plan = planExistente.get();
        plan.setNombre(planActualizado.getNombre());
        plan.setPrecio(planActualizado.getPrecio());
        plan.setDescripcion(planActualizado.getDescripcion());
        plan.setNumeroRevisiones(planActualizado.getNumeroRevisiones());
        plan.setArchivosIncluidos(planActualizado.getArchivosIncluidos());
        plan.setCaracteristicas(planActualizado.getCaracteristicas());
        plan.setEsRecomendado(planActualizado.getEsRecomendado());
        plan.setEstaActivo(planActualizado.getEstaActivo());
        plan.setColorBadge(planActualizado.getColorBadge());
        
        Plan planGuardado = planRepository.save(plan);
        return ResponseEntity.ok(planGuardado);
    }

    // API REST para eliminar un plan
    @DeleteMapping("/api/planes/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarPlan(@PathVariable Long id) {
        Optional<Plan> plan = planRepository.findById(id);
        if (plan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        planRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
