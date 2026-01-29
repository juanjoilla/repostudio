package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Plan;
import com.artesluis.artesluis_backend.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    
    @Autowired
    private PlanRepository planRepository;
    
    @Override
    public List<Plan> obtenerTodosLosPlanesActivos() {
        return planRepository.findByEstaActivoTrue();
    }
    
    @Override
    public List<Plan> obtenerPlanesOrdenadosPorRecomendacion() {
        return planRepository.findActiveOrderByRecommendedFirst();
    }
    
    @Override
    public Plan obtenerPlanPorId(Long id) {
        return planRepository.findByIdAndEstaActivoTrue(id);
    }
    
    @Override
    public Plan guardarPlan(Plan plan) {
        return planRepository.save(plan);
    }
    
    @Override
    public void eliminarPlan(Long id) {
        Plan plan = planRepository.findById(id).orElse(null);
        if (plan != null) {
            plan.setEstaActivo(false);
            planRepository.save(plan);
        }
    }
    
    @Override
    public void inicializarPlanesDefault() {
        if (planRepository.count() == 0) {
            // Plan Básico
            Plan basico = new Plan(
                "Básico",
                new BigDecimal("99.00"),
                "Plan ideal para emprendedores que necesitan un diseño profesional y económico",
                3,
                "Archivos PNG y JPG",
                "Diseño de logo;3 revisiones;Archivos PNG y JPG;Soporte por email",
                false,
                "primary"
            );
            
            // Plan Profesional
            Plan profesional = new Plan(
                "Profesional",
                new BigDecimal("199.00"),
                "La opción más popular para empresas que buscan una imagen corporativa completa",
                5,
                "Archivos PNG, JPG y vectoriales",
                "Todo del plan Básico;Branding completo;5 revisiones;Archivos vectoriales;Soporte telefónico",
                true,
                "warning"
            );
            
            // Plan Premium
            Plan premium = new Plan(
                "Premium",
                new BigDecimal("399.00"),
                "Solución integral para empresas que requieren presencia digital completa",
                -1, // Revisiones ilimitadas
                "Todos los archivos + material publicitario",
                "Todo del plan Profesional;Diseño web básico;Revisiones ilimitadas;Material publicitario;Consultoría personalizada",
                false,
                "success"
            );
            
            planRepository.save(basico);
            planRepository.save(profesional);
            planRepository.save(premium);
        }
    }
}