package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    List<Plan> findByEstaActivoTrue();
    
    List<Plan> findByEstaActivoTrueOrderByPrecioAsc();
    
    @Query("SELECT p FROM Plan p WHERE p.estaActivo = true ORDER BY p.esRecomendado DESC, p.precio ASC")
    List<Plan> findActiveOrderByRecommendedFirst();
    
    Plan findByIdAndEstaActivoTrue(Long id);
}