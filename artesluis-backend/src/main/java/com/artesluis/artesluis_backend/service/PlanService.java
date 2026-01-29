package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Plan;
import java.util.List;

public interface PlanService {
    
    List<Plan> obtenerTodosLosPlanesActivos();
    
    List<Plan> obtenerPlanesOrdenadosPorRecomendacion();
    
    Plan obtenerPlanPorId(Long id);
    
    Plan guardarPlan(Plan plan);
    
    void eliminarPlan(Long id);
    
    void inicializarPlanesDefault();
}