package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    
    @Query("SELECT ic FROM ItemCarrito ic WHERE ic.carrito.sessionId = :sessionId AND ic.plan.id = :planId")
    Optional<ItemCarrito> findBySessionIdAndPlanId(@Param("sessionId") String sessionId, @Param("planId") Long planId);
    
    void deleteByCarritoSessionId(String sessionId);
}