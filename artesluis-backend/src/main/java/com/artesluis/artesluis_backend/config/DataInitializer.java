package com.artesluis.artesluis_backend.config;

import com.artesluis.artesluis_backend.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    
    @Autowired
    private PlanService planService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Solo inicializar si no hay planes en la BD (fallback)
        // Los datos principales se cargan desde data.sql
        planService.inicializarPlanesDefault();
    }
}